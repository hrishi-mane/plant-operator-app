package com.example.plantoperator.POJO;

public class SessionDetails {
    public PlantDetails plantDetails;
    public String session_id;

    public SessionDetails() {
    }

    public SessionDetails(PlantDetails plantDetails,String session_id) {
        this.plantDetails = plantDetails;
        this.session_id = session_id;
    }

    public PlantDetails getPlantDetails() {
        return plantDetails;
    }

    public String getSession_id() {
        return session_id;
    }
}
