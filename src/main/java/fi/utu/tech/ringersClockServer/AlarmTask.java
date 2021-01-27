package fi.utu.tech.ringersClockServer;

import java.util.ArrayList;

/**
 * This class handles the alarming of the desired group!
 */

public class AlarmTask implements Runnable {

    private ArrayList<ClientThread> clients;

    public AlarmTask(ArrayList<ClientThread> clients){
        this.clients = clients;
    }

    public void run(){
        boolean leaderDecision = clients.get(0).confirmAlarm();
        if(!leaderDecision) return;
        for(ClientThread c : clients){
            c.alarm();
        }
    }
}
