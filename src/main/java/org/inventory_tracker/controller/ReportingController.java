package org.inventory_tracker.controller;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.inventory_tracker.dto.response.AttendantReportResponse;
import org.inventory_tracker.dto.response.DashboardResponse;
import org.inventory_tracker.dto.response.ExecutiveSummaryResponse;
import org.inventory_tracker.dto.response.InventoryReportResponse;
import org.inventory_tracker.dto.response.ProductReportResponse;
import org.inventory_tracker.dto.response.PumpReportResponse;
import org.inventory_tracker.dto.response.StationReportResponse;
import org.inventory_tracker.service.ReportingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiSuccessResponse<DashboardResponse>>
    getDashboard() {

        DashboardResponse response =
                reportingService.getDashboard();

        return ResponseEntity.ok(

                new ApiSuccessResponse<>(

                        LocalDateTime.now(),

                        HttpStatus.OK.value(),

                        "Dashboard retrieved successfully.",

                        response
                )
        );
    }

    @GetMapping("/executive")
    public ResponseEntity<ApiSuccessResponse<ExecutiveSummaryResponse>>
    getExecutiveSummary() {

        ExecutiveSummaryResponse response =
                reportingService.getExecutiveSummary();

        return ResponseEntity.ok(

                new ApiSuccessResponse<>(

                        LocalDateTime.now(),

                        HttpStatus.OK.value(),

                        "Executive summary retrieved successfully.",

                        response
                )
        );
    }

    @GetMapping("/station")
    public ResponseEntity<ApiSuccessResponse<List<StationReportResponse>>>
    getStationReport() {

        List<StationReportResponse> response =
                reportingService.getStationReport();

        return ResponseEntity.ok(

                new ApiSuccessResponse<>(

                        LocalDateTime.now(),

                        HttpStatus.OK.value(),

                        "Station report retrieved successfully.",

                        response
                )
        );
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiSuccessResponse<List<InventoryReportResponse>>>
    getInventoryReport() {

        List<InventoryReportResponse> response =
                reportingService.getInventoryReport();

        return ResponseEntity.ok(

                new ApiSuccessResponse<>(

                        LocalDateTime.now(),

                        HttpStatus.OK.value(),

                        "Inventory report retrieved successfully.",

                        response
                )
        );
    }

    @GetMapping("/product")
    public ResponseEntity<ApiSuccessResponse<List<ProductReportResponse>>>
    getProductReport() {

        List<ProductReportResponse> response =
                reportingService.getProductReport();

        return ResponseEntity.ok(

                new ApiSuccessResponse<>(

                        LocalDateTime.now(),

                        HttpStatus.OK.value(),

                        "Product report retrieved successfully.",

                        response
                )
        );
    }

    @GetMapping("/pump")
    public ResponseEntity<ApiSuccessResponse<List<PumpReportResponse>>>
    getPumpReport() {

        List<PumpReportResponse> response =
                reportingService.getPumpReport();

        return ResponseEntity.ok(

                new ApiSuccessResponse<>(

                        LocalDateTime.now(),

                        HttpStatus.OK.value(),

                        "Pump report retrieved successfully.",

                        response
                )
        );
    }

    @GetMapping("/attendant")
    public ResponseEntity<ApiSuccessResponse<List<AttendantReportResponse>>>
    getAttendantReport() {

        List<AttendantReportResponse> response =
                reportingService.getAttendantReport();

        return ResponseEntity.ok(

                new ApiSuccessResponse<>(

                        LocalDateTime.now(),

                        HttpStatus.OK.value(),

                        "Attendant report retrieved successfully.",

                        response
                )
        );
    }

}
