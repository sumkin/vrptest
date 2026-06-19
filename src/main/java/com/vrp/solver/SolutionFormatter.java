package com.vrp.solver;

import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.DeliveryActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SolutionFormatter {

    private static final LocalDateTime REFERENCE = LocalDateTime.of(2026, 1, 1, 0, 0);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void print(VehicleRoutingProblemSolution solution) {
        List<VehicleRoute> routes = new ArrayList<>(solution.getRoutes());
        routes.sort(Comparator.comparing(r -> r.getVehicle().getId()));

        System.out.printf("%-6s %-35s %-10s %-8s %-26s %-26s %-26s%n",
                "Route", "Vehicle", "Job", "Location-abbr", "Pickup arrive", "Pickup end", "Deliver arrive / end");
        System.out.println("-".repeat(140));

        int routeNo = 1;
        for (VehicleRoute route : routes) {
            String vehicle = route.getVehicle().getId();

            String pickupJob = "—", pickupLoc = "—", pickupArr = "—", pickupEnd = "—";
            String deliverLoc = "—", deliverArr = "—", deliverEnd = "—";

            for (TourActivity act : route.getActivities()) {
                if (act instanceof PickupActivity pa) {
                    pickupJob = pa.getJob().getId();
                    pickupLoc = abbrev(act.getLocation().getId());
                    pickupArr = toDateTime(act.getArrTime());
                    pickupEnd = toDateTime(act.getEndTime());
                } else if (act instanceof DeliveryActivity da) {
                    deliverLoc = abbrev(act.getLocation().getId());
                    deliverArr = toDateTime(act.getArrTime());
                    deliverEnd = toDateTime(act.getEndTime());
                }
            }

            System.out.printf("%-6d %-35s %-10s %-14s %-26s %-26s %s → %s%n",
                    routeNo++, vehicle, pickupJob, deliverLoc,
                    pickupArr, pickupEnd, deliverArr, deliverEnd);
        }

        System.out.println("-".repeat(140));
        System.out.printf("Total cost: %.0f  |  Routes: %d  |  Unassigned: %d%n",
                solution.getCost(), routes.size(), solution.getUnassignedJobs().size());

        if (!solution.getUnassignedJobs().isEmpty()) {
            System.out.println("\nUnassigned jobs:");
            solution.getUnassignedJobs().stream()
                    .map(Job::getId)
                    .sorted()
                    .forEach(id -> System.out.println("  " + id));
        }
    }

    private static String toDateTime(double seconds) {
        if (seconds <= 0 || seconds >= 1e8) return "—";
        return REFERENCE.plusSeconds((long) seconds).format(FMT);
    }

    private static String abbrev(String locationId) {
        // Keep last segment after last underscore for brevity
        int idx = locationId.lastIndexOf('_');
        return idx >= 0 ? locationId.substring(idx + 1) : locationId;
    }
}
