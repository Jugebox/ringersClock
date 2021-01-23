package fi.utu.tech.ringersClock.entities;

import java.io.Serializable;

public class RequestInfo implements Serializable {

    private Actions action;
    private WakeUpGroup group;

    public RequestInfo(Actions action, WakeUpGroup group){
        this.action = action;
        this.group = group;
    }

    public Actions getAction(){
        return action;
    }

    public WakeUpGroup getGroup(){
        return group;
    }
}