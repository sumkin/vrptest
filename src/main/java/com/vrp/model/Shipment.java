package com.vrp.model;

import java.time.LocalDate;

public class Shipment {
    public final String orderId;
    public final String sourceName;
    public final String customerName;
    public final String productName;
    public final int quantity;
    public final LocalDate orderDate;
    public final LocalDate earliestPickup;
    public final LocalDate latestPickup;

    public Shipment(
            String orderId,
            String sourceName,
            String customerName,
            String productName,
            int quantity,
            LocalDate orderDate,
            LocalDate earliestPickup,
            LocalDate latestPickup) {
        this.orderId = orderId;
        this.sourceName = sourceName;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.earliestPickup = earliestPickup;
        this.latestPickup = latestPickup;
    }

    @Override
    public String toString() {
        return orderId + ": " + sourceName + " -> " + customerName
                + " [" + earliestPickup + " - " + latestPickup + "], qty=" + quantity;
    }
}
