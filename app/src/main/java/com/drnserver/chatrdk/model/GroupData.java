package com.drnserver.chatrdk.model;

import java.util.ArrayList;

/**
 * Created by DrN on 4/9/2018.
 */

public class GroupData {
    private String groupName;
    private ArrayList<String> preferences;
    public GroupData(String newGroupName) {
        this.groupName = newGroupName;
        this.preferences = new ArrayList<String>();
    }

    public String getGroupName() {
        return groupName;
    }
    public ArrayList<String> getPreferences() {
        return preferences;
    }

    public void setGroupName(String newName) {
         this.groupName = newName;
    }

    public void setPreferences(ArrayList<String> newPrefs) {
        preferences = newPrefs;
    }
}
