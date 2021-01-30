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

    public void run(){
        try {
            //setting alarmtime to true
            var leader = clients.get(0);
            WeatherData wd = ws.getWeather();
            if(wg.isNotRaining() != wd.isNotRaining() || wg.isTemp() != wd.isTemperature()){
                for(ClientThread c : clients){
                    c.setAlarmCanceled();
                }
            }
            leader.setAlarmTime();
            System.out.println("Here");
            while (leader.getWait()){
                System.out.println("stalling");
            };
            boolean leaderDecision = leader.getAlarmAll();
            System.out.println("Alarmtask decision " + leaderDecision);
            if(!leaderDecision) {
                for(ClientThread c : clients){
                    c.cancelAlarm();
                }
            }
            else {
                for (ClientThread c : clients) {
                    System.out.println("plz");
                    c.setAlarmTime();
                }
            }
            leader.setAlarmAll(false);
        } catch (IOException e){
            System.out.println("Vittu IO");
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            System.out.println("Vittu ClassNotFound");
            e.printStackTrace();
        }
    }
}
