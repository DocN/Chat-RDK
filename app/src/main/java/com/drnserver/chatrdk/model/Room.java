package com.drnserver.chatrdk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Room {
    public ArrayList<String> member;
    public Map<String, String> groupInfo;
    public ArrayList<String> MatchedPreferences;

    public Room(){
        member = new ArrayList<>();
        MatchedPreferences = new ArrayList<>();
        groupInfo = new HashMap<String, String>();
    }
}
