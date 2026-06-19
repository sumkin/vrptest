package com.vrp.reader;

import com.vrp.model.AssetAssignment;
import com.vrp.model.TransportationAsset;
import com.vrp.model.Shipment;
import com.vrp.model.Site;
import com.vrp.model.TransitLane;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static InputData read(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
            Workbook wb = new XSSFWorkbook(fis)) {

            List<String> products = readProducts(wb.getSheet("Products"));
            List<Site> sites = readSites(wb.getSheet("Sites"));
            List<Shipment> shipments = readShipments(wb.getSheet("Shipments"));
            List<TransitLane> transitLanes = readTransitLanes(wb.getSheet("Transit Matrix"));
            List<TransportationAsset> transportationAssets = readTransportationAssets(wb.getSheet("Transportation Asset"));
            List<AssetAssignment> assetAssignments = readAssetAssignments(wb.getSheet("Asset Assignment"));

            return new InputData(products, sites, shipments, transitLanes, transportationAssets, assetAssignments);
        }
    }

    // ---- Products ----
    private static List<String> readProducts(Sheet sheet) {
        List<String> result = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) break;
            String name = str(row.getCell(0));
            if (name == null || name.isBlank()) break;
            result.add(name);
        }
        return result;
    }

    // ---- Sites ----
    // Columns: SiteName, SiteType, Address, Zipcode, City, State, Country, Latitude, Longitude, FixedServiceTimeUnload(min)
    private static List<Site> readSites(Sheet sheet) {
        List<Site> result = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String name = str(row.getCell(0));
            if (name == null || name.isBlank()) continue;
            result.add(new Site(
                    name,
                    str(row.getCell(1)),
                    str(row.getCell(4)),
                    str(row.getCell(5)),
                    num(row.getCell(7)),
                    num(row.getCell(8)),
                    (int) num(row.getCell(9))
            ));
        }
        return result;
    }

    // ---- Shipments ----
    // Columns: OrderId, SourceName, CustomerName, ProductName, Quantity, OrderDate, EarliestPickupDate, LatestPickupDate
    private static List<Shipment> readShipments(Sheet sheet) {
        List<Shipment> result = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String orderId = str(row.getCell(0));
            if (orderId == null || orderId.isBlank()) continue;
            result.add(new Shipment(
                    orderId,
                    str(row.getCell(1)),
                    str(row.getCell(2)),
                    str(row.getCell(3)),
                    (int) num(row.getCell(4)),
                    parseDate(row.getCell(5)),
                    parseDate(row.getCell(6)),
                    parseDate(row.getCell(7))
            ));
        }
        return result;
    }

    // ---- Transit Matrix ----
    // Columns: SourceName, DestinationName, Asset, OverrideCost, AdditiveCost, TravelTime(h),
    //          TravelTimeFactor, Distance, IsSymmetric, Status
    private static List<TransitLane> readTransitLanes(Sheet sheet) {
        List<TransitLane> result = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String status = str(row.getCell(9));
            if (!"Include".equalsIgnoreCase(status)) continue;
            result.add(new TransitLane(
                    str(row.getCell(0)),
                    str(row.getCell(1)),
                    str(row.getCell(2)),
                    num(row.getCell(3)),
                    num(row.getCell(5)),
                    num(row.getCell(7))
            ));
        }
        return result;
    }

    // ---- Transportation Asset ----
    // Columns: AssetName, FixedAssetCost, IsRoundTrip, CapacityQuantity,
    //          MaxDutyTimeBeforeRestTime(h), RestTime(h), MaxTimePerAsset(h),
    //          FixedServiceTimeLoad(min), Status
    private static List<TransportationAsset> readTransportationAssets(Sheet sheet) {
        List<TransportationAsset> result = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String status = str(row.getCell(8));
            if (!"Include".equalsIgnoreCase(status)) continue;
            Cell maxTimeCell = row.getCell(6);
            Double maxTime = (maxTimeCell == null || maxTimeCell.getCellType() == CellType.BLANK) ? null : num(maxTimeCell);
            result.add(new TransportationAsset(
                    str(row.getCell(0)),
                    num(row.getCell(1)),
                    "Yes".equalsIgnoreCase(str(row.getCell(2))),
                    (int) num(row.getCell(3)),
                    num(row.getCell(4)),
                    num(row.getCell(5)),
                    maxTime,
                    (int) num(row.getCell(7))
            ));
        }
        return result;
    }

    // ---- Asset Assignment ----
    // Columns: AssetName, SourceSite, DestinationSite, AssignedUnits, Linehaul, RateName, Status
    private static List<AssetAssignment> readAssetAssignments(Sheet sheet) {
        List<AssetAssignment> result = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String status = str(row.getCell(6));
            if (!"Include".equalsIgnoreCase(status)) continue;
            result.add(new AssetAssignment(
                    str(row.getCell(0)),
                    str(row.getCell(1)),
                    str(row.getCell(2)),
                    (int) num(row.getCell(3))
            ));
        }
        return result;
    }

    // ---- Helpers ----

    private static String str(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double v = cell.getNumericCellValue();
                yield (v == Math.floor(v)) ? String.valueOf((long) v) : String.valueOf(v);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private static double num(Cell cell) {
        if (cell == null) return 0.0;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try { yield Double.parseDouble(cell.getStringCellValue().trim()); }
                catch (NumberFormatException e) { yield 0.0; }
            }
            default -> 0.0;
        };
    }

    private static LocalDate parseDate(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        String s = str(cell);
        if (s == null || s.isBlank()) return null;
        // handles "2026-01-05 00:00:00" or "2026-01-05"
        if (s.length() > 10) s = s.substring(0, 10) + " 00:00:00";
        return LocalDate.parse(s, DATE_FMT);
    }
}
