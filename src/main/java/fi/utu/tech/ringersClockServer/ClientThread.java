package fi.utu.tech.ringersClockServer;

import  java.net.Socket;
import java.io.IOException;

public class ClientThread implements Runnable {

    private Socket clientSocket;

    public ClientThread() {
        super();
    }

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        System.out.println("Accepted Client Address - " + clientSocket.getInetAddress().getHostName());
        try {
            clientSocket.close();
            System.out.println("...Stopped");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
