package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClockServer.ClientThread;

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
				//accepting a new request with socket object
				Socket client = server.accept();
				System.out.println("New client connected " + client.getInetAddress().getHostName());

				ClientThread ct = new ClientThread(client);

				new Thread(ct).start();
			}
		}catch (Exception e){

		}

	}
}
