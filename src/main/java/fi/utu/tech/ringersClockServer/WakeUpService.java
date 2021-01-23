package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class WakeUpService extends Thread {

	private ArrayList<WakeUpGroup> groups = new ArrayList<>();
	private HashSet<UUID> clientsInGroups = new HashSet<>();

	public void addWakeUpGroup(WakeUpGroup wg) {
		groups.add(wg);
	}

	public boolean addToGroups(UUID id){
		return clientsInGroups.add(id);
	}

	public boolean removeFromGroups(UUID id){
		return clientsInGroups.remove(id);
	}

	public void addMember(UUID ID, ClientThread ct) {
		for(int i = 0; i < groups.size(); i++){
			if(groups.get(i).getID().compareTo(ID) == 0) {
				groups.get(i).addMemeber(ct.getID());
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

	public void removeMember(UUID memberId, UUID groupId){
		WakeUpGroup group = getWakeUpGroup(groupId);
		group.removeMember(memberId);
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