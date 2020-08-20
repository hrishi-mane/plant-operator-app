package com.example.plantoperator.POJO;

import com.google.firebase.firestore.GeoPoint;

public class CustomerDetails {

    public GeoPoint location;
    public String id;
    public String location_name;

    public CustomerDetails() {
    }

    public CustomerDetails(GeoPoint location, String id, String location_name) {
        this.location = location;
        this.id = id;
        this.location_name = location_name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    public String getLocation_name() {
        return location_name;
    }
}
