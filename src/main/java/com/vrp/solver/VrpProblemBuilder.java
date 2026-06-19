package com.vrp.solver;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.vrp.model.AssetAssignment;
import com.vrp.model.TransportationAsset;
import com.vrp.model.Site;
import com.vrp.model.TransitLane;
import com.vrp.reader.InputData;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class VrpProblemBuilder {

    private static final LocalDate REFERENCE_DATE = LocalDate.of(2026, 1, 1);

    public static VehicleRoutingProblem build(InputData data) {
        Map<String, Site> siteMap = new HashMap<>();
        for (Site s : data.sites) siteMap.put(s.name, s);

        Map<String, TransitLane> laneMap = new HashMap<>();
        for (TransitLane lane : data.transitLanes) laneMap.put(lane.key(), lane);

        Map<String, TransportationAsset> assetTypeMap = new HashMap<>();
        for (TransportationAsset at : data.transportationAssets) assetTypeMap.put(at.name, at);

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance()
                .setFleetSize(VehicleRoutingProblem.FleetSize.FINITE)
                .setRoutingCost(new TransitMatrixCosts(laneMap));

        // Vehicle types
        Map<String, VehicleType> vehicleTypes = new HashMap<>();
        for (TransportationAsset at : data.transportationAssets) {
            VehicleType type = VehicleTypeImpl.Builder.newInstance(at.name)
                    .addCapacityDimension(0, at.capacityQuantity)
                    .setFixedCost(at.fixedCost)
                    .build();
            vehicleTypes.put(at.name, type);
        }

        // Vehicles — one instance per assigned unit
        for (AssetAssignment aa : data.assetAssignments) {
            VehicleType type = vehicleTypes.get(aa.assetName);
            if (type == null) continue;
            TransportationAsset at = assetTypeMap.get(aa.assetName);
            Location depot = locationFor(aa.sourceSite, siteMap);
            for (int i = 1; i <= aa.assignedUnits; i++) {
                vrpBuilder.addVehicle(
                        VehicleImpl.Builder.newInstance(aa.assetName + "_" + i)
                                .setType(type)
                                .setStartLocation(depot)
                                .setReturnToDepot(false) // return cost is embedded in overrideCost; no return lanes exist
                                .build()
                );
            }
        }

        Map<String, Integer> siteLoadMinMap = new HashMap<>();
        for (AssetAssignment aa : data.assetAssignments) {
            TransportationAsset at = assetTypeMap.get(aa.assetName);
            if (at != null) siteLoadMinMap.put(aa.sourceSite, at.serviceTimeLoadMin);
        }

        // Shipment jobs
        for (com.vrp.model.Shipment order : data.shipments) {
            if (order.earliestPickup == null || order.latestPickup == null) continue;
            Site deliverySite = siteMap.get(order.customerName);
            double deliveryServiceSec = deliverySite != null ? deliverySite.serviceTimeUnloadMin * 60.0 : 0.0;
            double pickupServiceSec = siteLoadMinMap.getOrDefault(order.sourceName, 480) * 60.0;

            TimeWindow pickupTw = TimeWindow.newInstance(
                    toSeconds(order.earliestPickup),
                    toSeconds(order.latestPickup) + 86399.0  // end of latest pickup day
            );

            vrpBuilder.addJob(
                    Shipment.Builder.newInstance(order.orderId)
                            .setPickupLocation(locationFor(order.sourceName, siteMap))
                            .setDeliveryLocation(locationFor(order.customerName, siteMap))
                            .addSizeDimension(0, order.quantity)
                            .setPickupServiceTime(pickupServiceSec)
                            .setDeliveryServiceTime(deliveryServiceSec)
                            .addPickupTimeWindow(pickupTw)
                            .build()
            );
        }

        return vrpBuilder.build();
    }

    private static Location locationFor(String name, Map<String, Site> siteMap) {
        Site site = siteMap.get(name);
        if (site != null) {
            return Location.Builder.newInstance()
                    .setId(name)
                    .setCoordinate(Coordinate.newInstance(site.latitude, site.longitude))
                    .build();
        }
        return Location.newInstance(name);
    }

    private static double toSeconds(LocalDate date) {
        return (date.toEpochDay() - REFERENCE_DATE.toEpochDay()) * 86400.0;
    }
}
