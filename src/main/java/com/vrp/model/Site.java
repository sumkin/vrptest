package com.vrp.model;

public class Site {
    public final String name;
    public final String type;
    public final String city;
    public final String state;
    public final double latitude;
    public final double longitude;
    public final int serviceTimeUnloadMin;

    public Site(
            String name,
            String type,
            String city,
            String state,
            double latitude,
            double longitude,
            int serviceTimeUnloadMin) {
        this.name = name;
        this.type = type;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.serviceTimeUnloadMin = serviceTimeUnloadMin;
    }

    @Override
    public String toString() {
        return name + " (" + type + ", " + city + "/" + state + ")";
    }
}
