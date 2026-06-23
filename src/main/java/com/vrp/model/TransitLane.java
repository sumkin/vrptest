package com.vrp.model;

public class TransitLane {
    public final String sourceName;
    public final String destinationName;
    public final String asset;
    public final double overrideCost;
    public final double travelTimeHours;
    public final double distance;

    public TransitLane(
            String sourceName,
            String destinationName,
            String asset,
            double overrideCost,
            double travelTimeHours,
            double distance) {
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.asset = asset;
        this.overrideCost = overrideCost;
        this.travelTimeHours = travelTimeHours;
        this.distance = distance;
    }

    public String key() {
        return sourceName + "|" + destinationName + "|" + asset;
    }
}
