package com.drnserver.chatrdk.service;

/**
 * Created by ALEX on 2018-02-27.
 * This object holds all the users location info
 */

public class LocationInfo {


    // user latitude
    double lat;
    // user longitude
    double lon;
    // distance the users wants to match with
    int dist;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public LocationInfo() {

    }

    public LocationInfo(double lat, double lon, int dist) {
        this.lat = lat;

        this.lon = lon;
        this.dist = dist;
    }

}
