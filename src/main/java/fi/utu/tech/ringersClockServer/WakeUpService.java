package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.util.ArrayList;
import java.util.UUID;

public class WakeUpService extends Thread {

	private ArrayList<WakeUpGroup> groups = new ArrayList<>();

	public void addWakeUpGroup(WakeUpGroup wg) {
		groups.add(wg);
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

	/*public WakeUpGroup getWakeUpGroup(UUID ID){
		for(int i = 0; i < clientThreads.size(); i++){
			//if(clientThreads.get(i).getID().compareTo(ID) == 0) return wgList.get(i);
		}
		return null;
	}
	*/

}