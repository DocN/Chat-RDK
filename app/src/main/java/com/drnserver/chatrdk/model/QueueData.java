package com.drnserver.chatrdk.model;

import java.util.ArrayList;

/**
 * Created by DrN on 4/1/2018.
 */

public class QueueData {
    private String uid;
    private String lat;
    private String lon;
    private String distance;
    private ArrayList<String> premadePreferences;
    private ArrayList<String> chosenPreferences;

    public QueueData() {
        this.uid = "";
        this.lon = "";
        this.lat = "";
        this.distance = "";
        this.premadePreferences = new ArrayList<String>();
        this.chosenPreferences = new ArrayList<String>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public ArrayList<String> getPremadePreferences() {
        return premadePreferences;
    }

    public void setPremadePreferences(ArrayList<String> premadePreferences) {
        this.premadePreferences = premadePreferences;
    }

    public ArrayList<String> getChosenPreferences() {
        return chosenPreferences;
    }

    public void setChosenPreferences(ArrayList<String> chosenPreferences) {
        this.chosenPreferences = chosenPreferences;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean checkExistPremadePreference(String key) {
        for(int i =0; i < this.premadePreferences.size(); i++) {
            if(this.premadePreferences.get(i).equals(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkExistchosenPreferences(String key) {
        for(int i =0; i < this.chosenPreferences.size(); i++) {
            if(this.chosenPreferences.get(i).equals(key)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> returnCombinedPreferences() {
        ArrayList<String> combinedPreferences = new ArrayList<String>(premadePreferences);
        for(int i =0; i < chosenPreferences.size(); i++) {
            combinedPreferences.add(chosenPreferences.get(i));
        }
        return combinedPreferences;
    }
}
