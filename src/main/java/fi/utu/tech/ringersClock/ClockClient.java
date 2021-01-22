package fi.utu.tech.ringersClock;

/*
 * A class for handling network related stuff
 */

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class ClockClient extends Thread {

	private String host;
	private int port;
	private Gui_IO gio;
	private Socket socket;
	private PrintWriter writer;
	private boolean running = true;
	private ObjectOutputStream clientOutputStream;
	private ObjectInputStream clientInputStream;

	private boolean isInGroup = false;

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

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);

			clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
			clientInputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(running) {
			try{
				isInGroup = clientInputStream.readBoolean();
				System.out.println(isInGroup);
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		try{
			System.out.println("Closing connection to the server...");

			//Suljetaan streamit, jotta niihin käytetyt resurssit vapautuvat...
			clientOutputStream.close();
			clientInputStream.close();
			this.socket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public ArrayList<WakeUpGroup> createGroup(WakeUpGroup group) {
		try {
			System.out.println("Here 2");
			writer.println("create-group");

			//Lähetetään objekti...
			clientOutputStream.writeObject(group);

			//Luetaan takaisin tullut objekti...
			System.out.println("Here 2.5");
			ArrayList<WakeUpGroup> groups = (ArrayList<WakeUpGroup>) clientInputStream.readObject();
			System.out.println("Here 3");
			return groups;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	public ArrayList<WakeUpGroup> joinGroup(UUID groupId){
		writer.println("join-group");
		try{
			//Lähetetään objekti...
			clientOutputStream.writeObject(groupId);

			//Luetaan takaisin tullut objekti...
			ArrayList<WakeUpGroup> groups = (ArrayList<WakeUpGroup>) clientInputStream.readObject();

			return groups;
		}catch (IOException e){
			e.printStackTrace();
		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean getIsInGroup(){
		return this.isInGroup;
	}

	public void setIsInGroup(boolean bool){
		this.isInGroup = bool;
	}

	public void stopRunning(){
		this.running = false;
	}
}
