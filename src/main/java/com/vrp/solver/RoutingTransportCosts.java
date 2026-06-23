package com.vrp.solver;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.AbstractForwardVehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.driver.Driver;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.vrp.model.TransitLane;

import java.util.Map;

public class RoutingTransportCosts extends AbstractForwardVehicleRoutingTransportCosts {

    private static final double LARGE_COST = 1e9;
    private static final double LARGE_DISTANCE = 1e9;
    private static final double SECONDS_PER_HOUR = 3600.0;

    private final Map<String, TransitLane> laneMap;

    public RoutingTransportCosts(Map<String, TransitLane> laneMap) {
        this.laneMap = laneMap;
    }

    @Override
    public double getTransportCost(
            Location from,
            Location to,
            double departureTime,
            Driver driver,
            Vehicle vehicle) {
        if (from.getId().equals(to.getId())) return 0.0;
        String asset = vehicle != null ? vehicle.getType().getTypeId() : null;
        TransitLane lane = lookup(from.getId(), to.getId(), asset);
        return lane != null ? lane.overrideCost : LARGE_COST;
    }

    @Override
    public double getTransportTime(
            Location from,
            Location to,
            double departureTime,
            Driver driver,
            Vehicle vehicle) {
        if (from.getId().equals(to.getId())) return 0.0;
        String asset = vehicle != null ? vehicle.getType().getTypeId() : null;
        TransitLane lane = lookup(from.getId(), to.getId(), asset);
        return lane != null ? lane.travelTimeHours * SECONDS_PER_HOUR : LARGE_COST;
    }

    @Override
    public double getDistance(
            Location from,
            Location to,
            double departureTime,
            Vehicle vehicle) {
        if (from.getId().equals(to.getId())) return 0.0;
        String asset = vehicle != null ? vehicle.getType().getTypeId() : null;
        TransitLane lane = lookup(from.getId(), to.getId(), asset);
        return lane != null ? lane.distance : LARGE_DISTANCE;
    }

    private TransitLane lookup(String from, String to, String asset) {
        if (asset != null) {
            TransitLane lane = laneMap.get(from + "|" + to + "|" + asset);
            if (lane != null) return lane;
        }
        // fallback: first available lane for this origin-destination pair
        String prefix = from + "|" + to + "|";
        return laneMap.entrySet().stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
