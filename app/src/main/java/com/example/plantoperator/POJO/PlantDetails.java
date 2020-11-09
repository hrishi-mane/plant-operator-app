package com.example.plantoperator.POJO;


import com.google.firebase.firestore.GeoPoint;


public class PlantDetails {
    public GeoPoint location;
    public String plant_id;
    public String plant_name;


    public PlantDetails() {
    }

    public PlantDetails(GeoPoint location,String plant_id,String plant_name) {
        this.location = location;
        this.plant_id = plant_id;
        this.plant_name = plant_name;
    }

}
