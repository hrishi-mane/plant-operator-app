package com.example.plantoperator.POJO;

public class UserDetails {
    public String name;
    public String phone_number;
    public String city;
    public String address;

    public UserDetails() {
    }

    public UserDetails(String name, String phone_number, String city, String address) {
        this.name = name;
        this.phone_number = phone_number;
        this.city = city;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

}
