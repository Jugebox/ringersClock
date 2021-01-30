package fi.utu.tech.ringersClock.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseInfo implements Serializable {

    private boolean wantToAlarm;
    private boolean wantToConfirmAlarm;
    private boolean cancelAlarm;
    private WakeUpGroup group;
    ArrayList<WakeUpGroup> updatedGroups;

    public ResponseInfo(boolean wantToAlarm, boolean wantToConfirmAlarm, WakeUpGroup group, ArrayList<WakeUpGroup> updatedGroups){
        this.wantToAlarm = wantToAlarm;
        this.wantToConfirmAlarm = wantToConfirmAlarm;
        this.group = group;
        this.updatedGroups = updatedGroups;
    }

    public boolean alarm(){
        return wantToAlarm;
    }

    public boolean confirmAlarm() { return wantToConfirmAlarm; }

    public void setCancelAlarm(boolean cancelAlarm) {
        this.cancelAlarm = cancelAlarm;
    }

    public boolean getCancelAlarm() {
        return cancelAlarm;
    }

    public WakeUpGroup getGroup() {
        return group;
    }

    public ArrayList<WakeUpGroup> getUpdatedGroups(){
        return updatedGroups;
    }

}
