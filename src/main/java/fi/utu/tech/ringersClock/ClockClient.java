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
import java.util.concurrent.TimeUnit;

public class ClockClient extends Thread {

	private String host;
	private int port;
	private Gui_IO gio;
	private Socket socket;
	private boolean running = true;
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
		while(running) {
			try {
				//Luetaan takaisin tullut objekti...
				clientOutputStream.writeObject(new RequestInfo(Actions.UPDATE, null));
				ResponseInfo res = (ResponseInfo) clientInputStream.readObject();
				if(res.getUpdatedGroups().size() > 0) System.out.println(res.getUpdatedGroups().get(0).getName());
				gio.fillGroups(res.getUpdatedGroups());
				//Lähetetään update pyyntö 10 sekunnin välein serverille!
				TimeUnit.SECONDS.sleep(10);
			} catch (ClassNotFoundException e){
				e.printStackTrace();
			}
			catch (IOException e){
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			System.out.println("Try!!!");
			RequestInfo info = new RequestInfo(Actions.CREATE, group);
			//Lähetetään objekti...
			clientOutputStream.reset();
			clientOutputStream.writeObject(info);
			ResponseInfo res = (ResponseInfo) clientInputStream.readObject();
			gio.fillGroups(res.getUpdatedGroups());
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
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void stopRunning(){
		this.running = false;
	}
}
