package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.*;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class ClientThread extends Thread implements Serializable {

    private Socket clientSocket;
    private WakeUpService wup;
    private UUID ID;
    private UUID groupId = null;

    private boolean isInGroup = false;

    private boolean alarmTime = false;
    private boolean wait = true;
    private boolean alarmAll = false;

    private boolean alarmCanceled = false;

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
            while(!clientSocket.isClosed()){
                try {
                    //läheteään käyttäjälle tieto siitä kuuluko hän jo ryhmään
                    //selvitetään mitä käyttäjä haluaa tehdä
                    RequestInfo info = (RequestInfo) serverInputStream.readObject();
                    action = info.getAction();
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
                            System.out.println("updating...");
                            serverOutputStream.reset();
                            serverOutputStream.writeObject(new ResponseInfo(false, false, null, wup.getGroups()));
                            continue;
                        case CHECKALARM:
                            System.out.println("checking for alarm...");
                            confirmAlarm();
                            continue;
                        case CHECKALARM_MEMBER:
                            System.out.println("checking for member alarm...");
                            confirmMemberAlarm();
                            continue;
                        case ALARMALL:
                            System.out.println("alarming groups...");
                            alarm();
                            continue;
                        case CANCELALARM:
                            System.out.println("canceling alarm...");
                            cancelAlarm();
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
        serverOutputStream.writeObject(new ResponseInfo(false, false, null, wup.getGroups()));
    }

    private void resignGroup(){
        if(groupId == null) return;
        wup.removeMember(ID, groupId);
        wup.removeFromGroups(ID);
        groupId = null;
        try {
            serverOutputStream.writeObject(new ResponseInfo(false, false, null, wup.getGroups()));
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
                serverOutputStream.writeObject(new ResponseInfo(false, false, null, wup.getGroups()));
            }
            else {
                //Lisätään wakeup grouppiin käyttäjä ja lähetetään tiedot kaikista ryhmistä takaisin clientille
                group.addMemeber(this.ID);

                this.groupId = group.getID();

                wup.addWakeUpGroup(group, this);
                System.out.println(group.getName());

                System.out.println(wup.getGroups().get(0).getName());

                serverOutputStream.reset();
                serverOutputStream.writeObject(new ResponseInfo(false, false, null, wup.getGroups()));

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
                serverOutputStream.writeObject(new ResponseInfo(false, false, null, wup.getGroups()));
            }
            else {
                //Lisätään wakeup grouppiin käyttäjä ja lähetetään tiedot kaikista ryhmistä takaisin clientille
                group.addMemeber(this.ID);

                this.groupId = group.getID();

                wup.addMember(groupId, this);

                serverOutputStream.reset();
                serverOutputStream.writeObject(new ResponseInfo(false, false, null, wup.getGroups()));

                wup.printMembers();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void confirmAlarm() throws IOException, ClassNotFoundException {
        if(!alarmTime){
            serverOutputStream.reset();
            serverOutputStream.writeObject(new ResponseInfo(false, false, null, wup.getGroups()));
        }
        else {
            serverOutputStream.reset();
            serverOutputStream.writeObject(new ResponseInfo(false, true, wup.getWakeUpGroup(groupId), wup.getGroups()));
            this.alarmTime = false;
        }
    }

    public void confirmMemberAlarm() throws IOException, ClassNotFoundException {
        System.out.println(alarmTime);
        if(!alarmTime){
            serverOutputStream.reset();
            serverOutputStream.writeObject(new ResponseInfo(false, false, null, wup.getGroups()));
        }
       else if (alarmCanceled){
            serverOutputStream.reset();
            var info = new ResponseInfo(true, false, wup.getWakeUpGroup(groupId), wup.getGroups());
            info.setCancelAlarm(true);
            serverOutputStream.writeObject(info);
            this.alarmTime = false;

            //removing from group
            wup.removeFromGroups(this.ID);
            wup.removeMember(this.ID, groupId);
            this.groupId = null;
        }
        else {
            System.out.println("Member alarmed!!");
            serverOutputStream.reset();
            serverOutputStream.writeObject(new ResponseInfo(true, false, wup.getWakeUpGroup(groupId), wup.getGroups()));
            this.alarmTime = false;

            //removing from group
            wup.removeFromGroups(this.ID);
            wup.removeMember(this.ID, groupId);
            this.groupId = null;
        }
    }

    public void alarm() throws IOException, ClassNotFoundException {
        System.out.println("Alarmed!");
        this.alarmAll = true;
        this.wait = false;
        serverOutputStream.reset();
        serverOutputStream.writeObject(new ResponseInfo(true, false, wup.getWakeUpGroup(groupId), wup.getGroups()));

        //removing from group
        wup.removeFromGroups(this.ID);
        wup.removeMember(this.ID, groupId);
        this.groupId = null;
    }

    public void cancelAlarm() throws IOException, ClassNotFoundException{
        System.out.println("Canceled!");
        this.alarmAll = false;
        this.wait = false;
        serverOutputStream.reset();
        var res = new ResponseInfo(false, false, wup.getWakeUpGroup(groupId), wup.getGroups());
        res.setCancelAlarm(true);
        serverOutputStream.writeObject(res);

        //removing from group
        wup.removeFromGroups(this.ID);
        wup.removeMember(this.ID, groupId);
        this.groupId = null;
    }

    public void setAlarmCanceled(){
        System.out.println("cancel...");
        this.alarmCanceled = true;
    }

    public UUID getID(){
        return this.ID;
    }

    public void setAlarmTime() {
        this.alarmTime = true;
    }

    public boolean getWait(){
        return this.wait;
    }

    public void setAlarmAll(boolean b){
        this.alarmAll = b;
    }

    public boolean getAlarmAll() {
        return this.alarmAll;
    }

}
