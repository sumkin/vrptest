package com.vrp.model;

public class AssetAssignment {
    public final String assetName;
    public final String sourceSite;
    public final String destinationSite;
    public final int assignedUnits;
    public AssetAssignment(
            String assetName,
            String sourceSite,
            String destinationSite,
            int assignedUnits) {
        this.assetName = assetName;
        this.sourceSite = sourceSite;
        this.destinationSite = destinationSite;
        this.assignedUnits = assignedUnits;
    }
}
