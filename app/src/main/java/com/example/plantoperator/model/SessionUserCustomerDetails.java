package com.example.plantoperator.model;

public class SessionUserCustomerDetails {
    public UserDetails userDetails;

    public CustomerDetails customerDetails;



    public SessionUserCustomerDetails() {
    }

    public SessionUserCustomerDetails(UserDetails userDetails, CustomerDetails customerDetails) {
        this.userDetails = userDetails;
        this.customerDetails = customerDetails;
    }
}
