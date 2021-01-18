package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket clientSocket;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        System.out.println("Accepted Client Address - " + clientSocket.getInetAddress().getHostName());
        WakeUpGroup wug;
        try {
            //Luodaan output ja input streamit
            ObjectInputStream serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            //Luetaan clientiltä tullut objekti ja castatään se WakeUpGroupiksi
            wug = (WakeUpGroup)serverInputStream.readObject();

            //Asetetaan wakeupgroupin nimeksi "Penis" serverillä, koska olen henkiseltä iältäni 5 :D
            wug.setName("Penis");
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
        finally {
            try {
                clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
