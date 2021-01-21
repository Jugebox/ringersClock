package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientThread extends Thread {

    private Socket clientSocket;
    WakeUpService wup;
    private UUID ID;

    public ClientThread(Socket clientSocket, WakeUpService wup) {
        this.clientSocket = clientSocket;
        this.wup = wup;
        this.ID = UUID.randomUUID();
    }

    public void run() {
        System.out.println("Accepted Client Address - " + clientSocket.getInetAddress().getHostName());
        int i = 0;
        do {
            i++;
        } while(i < 2);
        //finally closing the socket connection, when user quits
        try {
            System.out.println("Client " + ID + " disconnected...");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewGroup() {
        WakeUpGroup wug;
        try {
            //Luodaan output ja input streamit
            ObjectInputStream serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            //Luetaan clientiltä tullut objekti ja castatään se WakeUpGroupiksi
            wug = (WakeUpGroup)serverInputStream.readObject();

            //Lisätään wakeup grouppiin käyttäjä
            wug.addMemeber(this.ID);
            serverOutputStream.writeObject(wug);

            //suljetaan streamit
            serverOutputStream.close();
            serverInputStream.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void joinGroup(String name) {

    }

    public UUID getID(){
        return this.ID;
    }

}
