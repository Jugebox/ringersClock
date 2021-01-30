package fi.utu.tech.ringersClock;

/*
 * A class for handling network related stuff
 */

import fi.utu.tech.ringersClock.entities.Actions;
import fi.utu.tech.ringersClock.entities.RequestInfo;
import fi.utu.tech.ringersClock.entities.ResponseInfo;
import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ClockClient extends Thread {

	private String host;
	private int port;
	private Gui_IO gio;
	private Socket socket;
	private boolean running = true;
	private boolean isLeader = false;
	private ObjectOutputStream clientOutputStream;
	private ObjectInputStream clientInputStream;

	public ClockClient(String host, int port, Gui_IO gio) {
		this.host = host;
		this.port = port;
		this.gio = gio;
	}

	public void run() {
		System.out.println("Host name: " + host + " Port: " + port + " Gui_IO:" + gio.toString());
		try {
			//Uusi soketti yhteys serveriin osoitteeseen 127.0.0.1 (joka viittaa tietokoneeseen itseensä), ja porttiin 3000
			this.socket = new Socket(host, port);
			gio.setClient(this);

			clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
			clientInputStream = new ObjectInputStream(socket.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		Code for polling the server
		 */

		var pollServer = new TimerTask() {
			public void run() {
				try {
					updateGroupList();
					if(isLeader) checkAlarm();
					else checkAlarmMember();
				}
				catch (IOException e){ e.printStackTrace(); }
				catch (ClassNotFoundException e){
					System.out.println("ÄÄÄÄH"); e.printStackTrace(); }
				catch(InterruptedException e){ e.printStackTrace(); }
			}
		};

		Timer pollTimer = new Timer(true);
		pollTimer.scheduleAtFixedRate(pollServer, 0, 1000 * 10);

		/*var pollAlarm = new TimerTask() {
			public void run() {
				checkAlarm();
			}
		};

		Timer alarmTimer = new Timer(true);
		alarmTimer.scheduleAtFixedRate(pollAlarm, 0, 1000 * 10);*/

		//===============================

		while(running) {
			/*try {

				ResponseInfo res = (ResponseInfo) clientInputStream.readObject();

				if(res.alarm()) gio.alarm();
				if(res.confirmAlarm()) gio.confirmAlarm(res.getGroup());
				if(res.getCancelAlarm()) gio.clearAlarmTime();

			} catch (ClassNotFoundException e){
				e.printStackTrace();
			}
			catch (IOException e){
				e.printStackTrace();
			}*/
		}
		try{
			System.out.println("Closing connection to the server...");

			//Suljetaan streamit, jotta niihin käytetyt resurssit vapautuvat...
			clientOutputStream.close();
			clientInputStream.close();
			socket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void createGroup(WakeUpGroup group) {
		try {
			RequestInfo info = new RequestInfo(Actions.CREATE, group);
			//Lähetetään objekti...
			clientOutputStream.reset();
			clientOutputStream.writeObject(info);
			ResponseInfo res = (ResponseInfo) clientInputStream.readObject();
			gio.fillGroups(res.getUpdatedGroups());
			this.isLeader = true;
			gio.appendToStatus("Group " + res.getGroup().getName() + " created!");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void joinGroup(WakeUpGroup group){
		try{
			RequestInfo info = new RequestInfo(Actions.JOIN, group);
			//Lähetetään objekti...
			clientOutputStream.reset();
			clientOutputStream.writeObject(info);
			//ja luetaan vastaus
			ResponseInfo res = (ResponseInfo) clientInputStream.readObject();
			gio.fillGroups(res.getUpdatedGroups());
			gio.appendToStatus("Group " + res.getGroup().getName() + " joined!");
		}catch (IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
	}

	public void resignGroup(){
		try {
			RequestInfo info = new RequestInfo(Actions.RESIGN, null);
			//Lähetetään objekti...
			clientOutputStream.reset();
			clientOutputStream.writeObject(info);
			this.isLeader = false;
			gio.appendToStatus("Group resigned!");
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void updateGroupList() throws IOException, ClassNotFoundException, InterruptedException{
		System.out.println("Polling...");
		//Luetaan takaisin tullut objekti...
		clientOutputStream.writeObject(new RequestInfo(Actions.UPDATE, null));
		ResponseInfo res = (ResponseInfo) clientInputStream.readObject();
		gio.fillGroups(res.getUpdatedGroups());
	}

	public void checkAlarm(){
		try {
			System.out.println("Checking for alarm...");
			//Luetaan takaisin tullut objekti...
			clientOutputStream.writeObject(new RequestInfo(Actions.CHECKALARM, null));
			ResponseInfo res = (ResponseInfo) clientInputStream.readObject();

			if(res.alarm()) gio.alarm();
			if (res.confirmAlarm()) {
				gio.confirmAlarm(res.getGroup());

			}
			if(res.getCancelAlarm()) gio.clearAlarmTime();
		}
		catch (IOException e){ e.printStackTrace(); }
		catch (ClassNotFoundException e){ e.printStackTrace(); }
	}

	public void checkAlarmMember() {
		try {
			System.out.println("Checking for member alarm...");
			//Luetaan takaisin tullut objekti...
			clientOutputStream.writeObject(new RequestInfo(Actions.CHECKALARM_MEMBER, null));
			ResponseInfo res = (ResponseInfo) clientInputStream.readObject();

			if(res.alarm()) {
				System.out.println("Client alarmed");
				gio.alarm();
			}
			if(res.getCancelAlarm()) gio.clearAlarmTime();
		}
		catch (IOException e){ e.printStackTrace(); }
		catch (ClassNotFoundException e){ e.printStackTrace(); }
	}

	public void alarmAll() throws IOException, ClassNotFoundException {
		System.out.println("ALARMING!!!");
		clientOutputStream.writeObject(new RequestInfo(Actions.ALARMALL, null));
		ResponseInfo res = (ResponseInfo) clientInputStream.readObject();
		gio.alarm();
		this.isLeader = false;
	}

	public void cancelAlarm(){
		try {
			clientOutputStream.writeObject(new RequestInfo(Actions.CANCELALARM, null));
			gio.clearAlarmTime();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void stopRunning(){
		this.running = false;
	}
}
