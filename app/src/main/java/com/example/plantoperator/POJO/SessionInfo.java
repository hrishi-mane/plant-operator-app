package com.example.plantoperator.POJO;

public class SessionInfo {

    public String session_id;
    public PlantDetails plantDetails;

    public SessionInfo() {
    }

    public SessionInfo(String session_id, PlantDetails plantDetails) {
        this.session_id = session_id;
        this.plantDetails = plantDetails;
    }
}
