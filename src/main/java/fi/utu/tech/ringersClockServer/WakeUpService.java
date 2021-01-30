package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WakeUpService extends Thread {

	private ArrayList<WakeUpGroup> groups = new ArrayList<>();
	private ArrayList<ClientThread> clients = new ArrayList<>();
	private HashSet<UUID> clientsInGroups = new HashSet<>();

	synchronized public void addWakeUpGroup(WakeUpGroup wg, ClientThread ct) {
		groups.add(wg);
		var duration = Duration.between(new Date().toInstant(), wg.getWakeUpTime().atZone(ZoneId.systemDefault()));
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

		long millisDelay = duration.toMillis();
		System.out.println(duration.toMinutes());

		AlarmTask task = new AlarmTask(wg);
		task.addMember(ct);
		wg.setAlarmTask(task);

		service.schedule(task, millisDelay, TimeUnit.MILLISECONDS);
	}

	synchronized public boolean addToGroups(UUID id){
		return clientsInGroups.add(id);
	}

	synchronized public void addToClientThreads(ClientThread ct) { clients.add(ct); }

	//Funktio jolla voidaan päivittää uudet tiedot eri ryhmistä kaikille käyttäjille!
	//Ehkä turha
	public void broadcastGroups() throws IOException {
		for (ClientThread c : clients){
			c.updateGroupList();
			//broadcasting to existing clients only
			for(UUID i : clientsInGroups){
				if(i.compareTo(c.getID()) == 0){
					c.updateGroupList();
				}
			}
		}
	}

	synchronized public boolean removeFromGroups(UUID id){
		return clientsInGroups.remove(id);
	}

	public void addMember(UUID ID, ClientThread ct) {
		for(int i = 0; i < groups.size(); i++){
			if(groups.get(i).getID().compareTo(ID) == 0) {
				groups.get(i).addMemeber(ct.getID());
				//adding to alarmtask
				groups.get(i).getAlarmTask().addMember(ct);
			}
		}
	}

	public ArrayList<WakeUpGroup> getGroups() {
		return this.groups;
	}

	public WakeUpGroup getWakeUpGroup(UUID ID){
		for(int i = 0; i < groups.size(); i++){
			if(groups.get(i).getID().compareTo(ID) == 0) return groups.get(i);
		}
		return null;
	}

	synchronized public void removeMember(UUID memberId, UUID groupId){
		WakeUpGroup group = getWakeUpGroup(groupId);
		int i = group.removeMember(memberId);
		group.getAlarmTask().removeMember(memberId);
		if(i == 0) removeGroup(group.getID());
	}

	synchronized public void removeGroup(UUID groupId){
		for(int i = 0; i < groups.size(); i++){
			WakeUpGroup g = groups.get(i);
			if(g.getID().compareTo(groupId) == 0){
				for(int j = 0; j < g.getMembers().size(); j++){
					removeFromGroups(g.getMembers().get(j));
				}
				groups.remove(i);
			}
		}
	}

	public void printMembers(){
		for(int i = 0; i < groups.size(); i++) {
			System.out.println("Members of group " + groups.get(i).getName());
			ArrayList<UUID> membs = groups.get(i).getMembers();
			for (int j = 0; j < membs.size(); j++){
				System.out.println(membs.get(j));
			}
		}
	}
}