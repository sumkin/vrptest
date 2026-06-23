package com.vrp.reader;

import com.vrp.model.AssetAssignment;
import com.vrp.model.TransportationAsset;
import com.vrp.model.Shipment;
import com.vrp.model.Site;
import com.vrp.model.TransitLane;

import java.util.List;

public class InputData {
    public final List<String> products;
    public final List<Site> sites;
    public final List<Shipment> shipments;
    public final List<TransitLane> transitLanes;
    public final List<TransportationAsset> transportationAssets;
    public final List<AssetAssignment> assetAssignments;

    public InputData(
            List<String> products,
            List<Site> sites,
            List<Shipment> shipments,
            List<TransitLane> transitLanes,
            List<TransportationAsset> transportationAssets,
            List<AssetAssignment> assetAssignments) {
        this.products = products;
        this.sites = sites;
        this.shipments = shipments;
        this.transitLanes = transitLanes;
        this.transportationAssets = transportationAssets;
        this.assetAssignments = assetAssignments;
    }

    @Override
    public String toString() {
        return String.format(
                "InputData{products=%d, sites=%d, shipments=%d," +
                " transitLanes=%d, transportationAssets=%d, assetAssignments=%d}",
                products.size(), sites.size(), shipments.size(),
                transitLanes.size(), transportationAssets.size(), assetAssignments.size());
    }
}
