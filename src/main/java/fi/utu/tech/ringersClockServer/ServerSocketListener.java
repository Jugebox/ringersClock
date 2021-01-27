package fi.utu.tech.ringersClockServer;

import java.net.Socket;
import java.net.ServerSocket;

public class ServerSocketListener extends Thread {

	private String host;
	private int port;
	private WakeUpService wup;

	private ServerSocket server = null;

	public ServerSocketListener(String host, int port, WakeUpService wup) {
		this.host = host;
		this.port = port;
		this.wup = wup;
	}

	public void run() {
		try{
			//creating a new socket for the server listening to port specified in the ServerApp class
			server = new ServerSocket(port);
			server.setReuseAddress(true);

			//infinite loop for accepting client request
			while(true){
				//Otetaan vastaan kutsu clientiltä ja luodaan soketti sen pohjalta
				Socket client = server.accept();
				System.out.println("New client connected " + client.getInetAddress().getHostName());

				//käynnistetään uusi client säie soketin kera
				ClientThread ct = new ClientThread(client, wup);
				ct.start();
				wup.addToClientThreads(ct);
			}
		} catch (Exception e){

		}

	}
}
