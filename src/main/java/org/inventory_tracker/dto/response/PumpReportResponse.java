package org.inventory_tracker.dto.response;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PumpReportResponse {

    private Long pumpId;

    private String pumpNumber;

    private String pumpName;

    private String stationName;

    private String productName;

    private Boolean active;

    private Long totalAssignments;

    private Long activeAssignments;

    private Long auditsCompleted;
}
