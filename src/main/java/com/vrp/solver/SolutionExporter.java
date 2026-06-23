package com.vrp.solver;

import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.DeliveryActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SolutionExporter {

    private static final LocalDateTime REFERENCE = LocalDateTime.of(2026, 1, 1, 0, 0);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void export(VehicleRoutingProblemSolution solution, String filePath) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle headerStyle = buildHeaderStyle(wb);
            CellStyle dateStyle = buildDateStyle(wb);

            writeRoutesSheet(wb, solution, headerStyle, dateStyle);
            writeUnassignedSheet(wb, solution, headerStyle);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }
        }
        System.out.println("Solution exported to: " + filePath);
    }

    // ---- Routes sheet ----

    private static void writeRoutesSheet(
            Workbook wb,
            VehicleRoutingProblemSolution solution,
            CellStyle headerStyle,
            CellStyle dateStyle) {
        Sheet sheet = wb.createSheet("Routes");
        String[] headers = {
            "Route", "Vehicle", "AssetType", "JobId",
            "PickupSite", "DeliverySite",
            "PickupEnd", "DeliveryArrive", "DeliveryEnd"
        };
        writeHeader(sheet.createRow(0), headers, headerStyle);

        List<VehicleRoute> routes = new ArrayList<>(solution.getRoutes());
        routes.sort(Comparator.comparing(r -> r.getVehicle().getId()));

        int rowNum = 1;
        for (int i = 0; i < routes.size(); i++) {
            VehicleRoute route = routes.get(i);
            String vehicle = route.getVehicle().getId();
            String assetType = route.getVehicle().getType().getTypeId();

            String jobId = "", pickupSite = "", deliverySite = "";
            double pickupEnd = -1, deliverArrTime = -1, deliverEndTime = -1;

            for (TourActivity act : route.getActivities()) {
                if (act instanceof PickupActivity pa) {
                    jobId = pa.getJob().getId();
                    pickupSite = act.getLocation().getId();
                    pickupEnd = act.getEndTime();
                } else if (act instanceof DeliveryActivity) {
                    deliverySite = act.getLocation().getId();
                    deliverArrTime = act.getArrTime();
                    deliverEndTime = act.getEndTime();
                }
            }

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(vehicle);
            row.createCell(2).setCellValue(assetType);
            row.createCell(3).setCellValue(jobId);
            row.createCell(4).setCellValue(pickupSite);
            row.createCell(5).setCellValue(deliverySite);
            setDateCell(row, 6, pickupEnd, dateStyle);
            setDateCell(row, 7, deliverArrTime, dateStyle);
            setDateCell(row, 8, deliverEndTime, dateStyle);
        }

        autoSize(sheet, headers.length);
    }

    // ---- Unassigned sheet ----

    private static void writeUnassignedSheet(
            Workbook wb,
            VehicleRoutingProblemSolution solution,
            CellStyle headerStyle) {
        Sheet sheet = wb.createSheet("Unassigned");
        String[] headers = {"JobId", "PickupSite", "DeliverySite", "Quantity"};
        writeHeader(sheet.createRow(0), headers, headerStyle);

        List<Job> unassigned = new ArrayList<>(solution.getUnassignedJobs());
        unassigned.sort(Comparator.comparing(Job::getId));

        int rowNum = 1;
        for (Job job : unassigned) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(job.getId());
            if (job instanceof Shipment s) {
                row.createCell(1).setCellValue(s.getPickupLocation().getId());
                row.createCell(2).setCellValue(s.getDeliveryLocation().getId());
                row.createCell(3).setCellValue(s.getSize().get(0));
            }
        }

        autoSize(sheet, headers.length);
    }

    // ---- Helpers ----

    private static void writeHeader(Row row, String[] headers, CellStyle style) {
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private static void setDateCell(Row row, int col, double seconds, CellStyle style) {
        Cell cell = row.createCell(col);
        if (seconds > 0 && seconds < 1e8) {
            cell.setCellValue(REFERENCE.plusSeconds((long) seconds).format(FMT));
            cell.setCellStyle(style);
        }
    }

    private static void autoSize(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) sheet.autoSizeColumn(i);
    }

    private static CellStyle buildHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static CellStyle buildDateStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }
}
