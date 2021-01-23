package fi.utu.tech.ringersClock.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseInfo implements Serializable {

    private boolean isInGroup;
    ArrayList<WakeUpGroup> updatedGroups;

    public ResponseInfo(boolean isInGroup, ArrayList<WakeUpGroup> updatedGroups){
        this.isInGroup = isInGroup;
        this.updatedGroups = updatedGroups;
    }

    public boolean getIsInGroup(){
        return isInGroup;
    }

    public ArrayList<WakeUpGroup> getUpdatedGroups(){
        return updatedGroups;
    }

}
