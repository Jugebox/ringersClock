package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.*;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientThread extends Thread {

    private Socket clientSocket;
    private WakeUpService wup;
    private UUID ID;
    private UUID groupId = null;

    private boolean isInGroup = false;

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
            serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
            serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            Actions action;
            System.out.println(clientSocket.isClosed());

            int i = 0;
            while(!clientSocket.isClosed()){
                System.out.println(i);
                i++;
                try {
                    //läheteään käyttäjälle tieto siitä kuuluko hän jo ryhmään
                    //selvitetään mitä käyttäjä haluaa tehdä
                    RequestInfo info = (RequestInfo) serverInputStream.readObject();
                    action = info.getAction();
                    System.out.println(action);
                    switch (action){
                        case CREATE:
                            System.out.println("creating a new group...");
                            createNewGroup(info.getGroup());
                            //wup.broadcastGroups();
                            System.out.println("group created!");
                            continue;
                        case JOIN:
                            System.out.println("joining group...");
                            joinGroup(info.getGroup());
                            continue;
                        case RESIGN:
                            System.out.println("resigning group...");
                            resignGroup();
                            continue;
                        case UPDATE:
                            serverOutputStream.reset();
                            serverOutputStream.writeObject(new ResponseInfo(false, wup.getGroups()));
                            continue;
                        default:
                            continue;
                    }
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
            }

            wup.removeMember(ID, groupId);
            wup.removeFromGroups(ID);
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

    public void updateGroupList() throws IOException {
        serverOutputStream.writeObject(new ResponseInfo(false, wup.getGroups()));
    }

    private void resignGroup(){
        if(groupId == null) return;
        wup.removeMember(ID, groupId);
        wup.removeFromGroups(ID);
        groupId = null;
        try {
            serverOutputStream.writeObject(new ResponseInfo(false, wup.getGroups()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createNewGroup(WakeUpGroup group) {
        try {
            //onko käyttäjä jo ryhmässä?
            boolean inGroup = wup.addToGroups(ID);

            System.out.println(inGroup);

            if(!inGroup){
                serverOutputStream.writeObject(new ResponseInfo(true, wup.getGroups()));
            }
            else {
                //Lisätään wakeup grouppiin käyttäjä ja lähetetään tiedot kaikista ryhmistä takaisin clientille
                group.addMemeber(this.ID);

                this.groupId = group.getID();

                wup.addWakeUpGroup(group);
                System.out.println(group.getName());

                System.out.println(wup.getGroups().get(0).getName());

                serverOutputStream.reset();
                serverOutputStream.writeObject(new ResponseInfo(false, wup.getGroups()));

                wup.printMembers();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void joinGroup(WakeUpGroup group) {
        try{
            //onko käyttäjä jo ryhmässä?
            boolean inGroup = wup.addToGroups(ID);

            if(!inGroup){
                serverOutputStream.writeObject(new ResponseInfo(true, wup.getGroups()));
            }
            else {
                //Lisätään wakeup grouppiin käyttäjä ja lähetetään tiedot kaikista ryhmistä takaisin clientille
                group.addMemeber(this.ID);

                this.groupId = group.getID();

                wup.addMember(groupId, this);

                serverOutputStream.reset();
                serverOutputStream.writeObject(new ResponseInfo(false, wup.getGroups()));

                wup.printMembers();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean confirmAlarm(){
        return true;
    }

    public void alarm(){

    }

    public UUID getID(){
        return this.ID;
    }

}
