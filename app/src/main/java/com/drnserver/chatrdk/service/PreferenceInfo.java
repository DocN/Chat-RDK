package com.drnserver.chatrdk.service;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-03-11.
 */

public class PreferenceInfo {
    ArrayList<String> premadePreferences;
    ArrayList<String> chosenPreferences;

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

    public PreferenceInfo(ArrayList<String> premadePreferences, ArrayList<String> chosenPreferences) {

        this.premadePreferences = premadePreferences;
        this.chosenPreferences = chosenPreferences;
    }
}
