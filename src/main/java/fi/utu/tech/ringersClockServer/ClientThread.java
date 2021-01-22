package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class ClientThread extends Thread {

    private Socket clientSocket;
    WakeUpService wup;
    private UUID ID;
    private UUID groupId = null;

    private ObjectInputStream serverInputStream;
    private ObjectOutputStream serverOutputStream;

    public ClientThread(Socket clientSocket, WakeUpService wup) {
        this.clientSocket = clientSocket;
        this.wup = wup;
        this.ID = UUID.randomUUID();
    }

    public void run() {
        System.out.println("Accepted Client Address - " + clientSocket.getInetAddress().getHostName());

        try {
            //streams
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            serverInputStream = new ObjectInputStream(input);
            serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            String action;
            System.out.println(clientSocket.isClosed());
            while(!clientSocket.isClosed()){
                action = reader.readLine().trim();
                System.out.println(action);
                if(action.equals("create-group")){
                    System.out.println("creating a new group...");
                    createNewGroup();
                }
                if(action.equals("join-group")){
                    System.out.println("joining group...");
                    joinGroup();
                }
                System.out.println(clientSocket.isClosed());
            }

            wup.removeMember(ID, groupId);
            wup.printMembers();
            groupId = null;

            //suljetaan streamit
            serverOutputStream.close();
            serverInputStream.close();
            System.out.println("Client " + ID + " disconnected...");
            clientSocket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewGroup() {
        WakeUpGroup wug;
        System.out.println("HEI VITTU");
        try {
            System.out.println("============================================");
            //Luetaan clientiltä tullut objekti ja castatään se WakeUpGroupiksi
            wug = (WakeUpGroup)serverInputStream.readObject();

            //Lisätään wakeup grouppiin käyttäjä ja lähetetään tiedot kaikista ryhmistä takaisin clientille
            wug.addMemeber(this.ID);
            this.groupId = wug.getID();
            wup.addWakeUpGroup(wug);
            System.out.println(wug.getName());
            serverOutputStream.writeObject(wup.getGroups());
            System.out.println("============================================");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void joinGroup() {
        UUID groupId;
        try{
            //reading group uuid from client
            groupId = (UUID) serverInputStream.readObject();
            this.groupId = groupId;
            wup.addMember(groupId, this);

            //sending backt the updated wakeupgroups
            serverOutputStream.writeObject(wup.getGroups());
        }catch (IOException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public UUID getID(){
        return this.ID;
    }

}
