package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;
import fi.utu.tech.weatherInfo.FMIWeatherService;
import fi.utu.tech.weatherInfo.WeatherData;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class handles the alarming of the desired group!
 */

public class AlarmTask implements Runnable, Serializable {

    private ArrayList<ClientThread> clients = new ArrayList<>();
    private WakeUpGroup wg;
    private FMIWeatherService ws;

    public AlarmTask (WakeUpGroup wg){
        this.wg = wg;
        ws = new FMIWeatherService();
    }

    public void addMember(ClientThread c){
        this.clients.add(c);
    }

    public void removeMember(UUID id){
        for(int i = 0; i < clients.size(); i++){
            if(clients.get(i).getID().equals(id)) clients.remove(i);
        }
    }

    public void cancelAlarms(){
        for(int i = 0; i < clients.size(); i++){
            if(i == 0) continue;
            System.out.println("cancer");
            clients.get(i).setAlarmCanceled();
        }
    }

    public void run(){
            //setting alarmtime to true
            var leader = clients.get(0);
            WeatherData wd = ws.getWeather();
            System.out.println("Group raining: " + wg.isNotRaining() + ", Data raining: " + wd.isNotRaining());
            System.out.println("Group temp: " + wg.isTemp() + ", Data temp: " + wd.isTemperature());

            if((wg.isNotRaining() && !wd.isNotRaining()) || (wg.isTemp() && !wd.isTemperature())){
                System.out.println("here...");
                for(ClientThread c : clients){
                    System.out.println("cancer");
                    c.setAlarmCanceled();
                }
                return;
            }

            leader.setAlarmTime();
            System.out.println("Waiting for leader decision...");
            while (leader.getWait()){
                System.out.println("stalling...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            };
            System.out.println("Decided!");

            boolean leaderDecision = leader.getAlarmAll();
            System.out.println("Alarmtask decision " + leaderDecision);
            if(!leaderDecision) {
                for(ClientThread c : clients){
                    c.setAlarmCanceled();
                }
            }
            else {
                for (ClientThread c : clients) {
                    System.out.println("Set and go");
                    c.setAlarmTime();
                }
            }
            leader.setAlarmAll(false);
    }
}
