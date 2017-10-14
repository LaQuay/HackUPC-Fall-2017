package dev.blind.hackupc.a2017.blindhelper.model;

import java.util.ArrayList;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class MyPlaces {
    private Double lat;
    private Double lng;
    private String address;
    private String name;
    private ArrayList<String> types;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setType(ArrayList<String> types) {
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
