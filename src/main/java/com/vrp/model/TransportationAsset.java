package com.vrp.model;

public class TransportationAsset {
    public final String name;
    public final double fixedCost;
    public final boolean isRoundTrip;
    public final int capacityQuantity;
    public final double maxDutyTimeHours;
    public final double restTimeHours;
    public final Double maxTimePerAssetHours;
    public final int serviceTimeLoadMin;

    public TransportationAsset(String name, double fixedCost, boolean isRoundTrip,
                     int capacityQuantity, double maxDutyTimeHours, double restTimeHours,
                     Double maxTimePerAssetHours, int serviceTimeLoadMin) {
        this.name = name;
        this.fixedCost = fixedCost;
        this.isRoundTrip = isRoundTrip;
        this.capacityQuantity = capacityQuantity;
        this.maxDutyTimeHours = maxDutyTimeHours;
        this.restTimeHours = restTimeHours;
        this.maxTimePerAssetHours = maxTimePerAssetHours;
        this.serviceTimeLoadMin = serviceTimeLoadMin;
    }
}
