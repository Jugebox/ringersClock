package fi.utu.tech.ringersClock;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

import fi.utu.tech.ringersClock.UI.MainViewController;
import fi.utu.tech.ringersClock.entities.WakeUpGroup;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Gui_IO {

	private MainViewController cont;
	private ClockClient client;

	public Gui_IO(MainViewController cont) {
		this.cont = cont;
	}

	/*
	 * Method for displaying the time of the alarm Use this method to set the alarm
	 * time in the UI The time is received from the server
	 * 
	 * DO not edit
	 */
	public void setAlarmTime(Instant time) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				cont.setAlarmTime(time);
			}
		});
	}

	
	/*
	 * Method for clearing the time of the alarm. Use this method when alarm is cancelled.
	 * 
	 * DO not edit
	 */
	public void clearAlarmTime() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				cont.clearAlarmTime();
			}
		});
	}
	
	/*
	 * Method for appending text to the status display You can write status messages
	 * to UI using this method.
	 * 
	 * DO not edit
	 */
	public void appendToStatus(String text) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				cont.appendToStatus(text);
			}
		});
	}

	/*
	 * Method for filling the existing wake-up groups to Choosebox Must run every
	 * time when the existing wake-up groups are receiver from the server
	 *
	 *DO not edit
	 */
	public void fillGroups(ArrayList<WakeUpGroup> list) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				cont.fillGroups(list);
			}
		});
	}

	/*
	 * This method is for waking up the ringer when it is time.
	 * 
	 * DO not edit
	 */

	public void alarm() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				cont.alarm();
				cont.clearAlarmTime();
			}
		});
	}

	/*
	 * This method must only on the client is the group leader. The idea is to use
	 * this method to confirm the wake up before waking up the rest of the team
	 * 
	 * DO not edit
	 */

	public void confirmAlarm(WakeUpGroup group) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirm alarm");
				alert.setHeaderText("Do you want wake up the team?");
				alert.setContentText("The weather seems to be ok.");
				alert.showAndWait().ifPresent((btnType) -> {
					if (btnType == ButtonType.OK) {
						AlarmAll(group);
					} else {
						CancelAlarm(group);
					}
				});
			}
		});
	}

	/*
	 * This method is run if the leader accepts the wake-up Now you have to wake up
	 * the rest of the team
	 * 
	 * IMPLEMENT THIS ONE
	 */
	public void AlarmAll(WakeUpGroup group) {
		System.out.println("AlarmAll " + group.getName());
	}

	/*
	 * This method is run if the leader cancel the wake-up The alarm is cancelled
	 * and should be removed from server
	 * 
	 * IMPLEMENT THIS ONE
	 */
	public void CancelAlarm(WakeUpGroup group) {
		System.out.println("CancelAll " + group.getName());
	}

	/*
	 * This method is run when user pressed the create button Now the group with
	 * wake-up time must be sent to server
	 * 
	 * IMPLEMENT THIS ONE
	 */
	public void createNewGroup(String name, Integer hour, Integer minutes, boolean notRaining, boolean temp) {
		if(client.getIsInGroup()) return;
		UUID id =  UUID.randomUUID();
		WakeUpGroup group = new WakeUpGroup(id, name, hour, minutes);

		System.out.println("Create New Group pressed, name: " + name + " Wake-up time: " + hour + ":" + minutes + " Rain allowed: " + notRaining + " Temperature over 0 deg: " + temp);
		System.out.println("Id: " + id);

		int alarmHour;
		if (hour == 1) alarmHour = 23;
		else if (hour == 0) alarmHour = 22;
		else alarmHour = hour - 2;

		try {
			ArrayList<WakeUpGroup> groups = client.createGroup(group);
			fillGroups(groups);
			System.out.println("Here 1");
			if (hour < 10) {
				CharSequence alarmTime = "2021-01-22T0" + alarmHour + ":" + minutes + ":00Z";
				Instant time = Instant.parse(alarmTime);
				System.out.println(time);
				cont.setAlarmTime(time);
			}
			else {
				CharSequence alarmTime = "2021-01-22T" + alarmHour + ":" + minutes + ":00Z";
				Instant time = Instant.parse(alarmTime);
				System.out.println(time);
				cont.setAlarmTime(time);
			}
		} catch (Exception e) {System.out.println(e); }


		//2011-12-03T10:15:30Z
		//CharSequence alarmTime = "2021-01-22T" + hour + ":" + minutes + ":00Z";
		//Instant time = Instant.parse(alarmTime);

		//cont.setAlarmTime(time);
	}

	/*
	 * This method is run when user pressed the join button The info must be sent to
	 * server
	 * 
	 * IMPLEMENT THIS ONE
	 */

	public void joinGroup(WakeUpGroup group) {
		if(client.getIsInGroup()) return;
		System.out.println("Join Group pressed" + group.getName());
		try {
			client.joinGroup(group.getID());
		} catch (Exception e) {System.out.println(e); }
	}
	
	/*
	 * This method is run when user pressed the resign button The info must be sent to
	 * server
	 * 
	 * IMPLEMENT THIS ONE
	 */
	public void resignGroup() {
		System.out.println("Resign Group pressed");
		client.setIsInGroup(false);
	}

	public void setClient(ClockClient client){
		this.client = client;
	}
}
