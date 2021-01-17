package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.util.ArrayList;

public class WakeUpService extends Thread {

	ArrayList<WakeUpGroup> wgList = new ArrayList<>();

	public WakeUpService() {

	}

	public void run() {
		while(true){

		}
	}

	public void addWakeUpGroup(WakeUpGroup wg) {
		wgList.add(wg);
	}

	public WakeUpGroup getWakeUpGroup(Integer ID){
		for(int i = 0; i < wgList.size(); i++){
			if(wgList.get(i).getID() == ID) return wgList.get(i);
		}
		return null;
	}

}
