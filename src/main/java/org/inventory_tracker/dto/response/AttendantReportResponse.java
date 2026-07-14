package org.inventory_tracker.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendantReportResponse {

    private Long attendantId;

    private String username;

    private String fullName;

    private String stationName;

    private String assignedPump;

    private Boolean active;

    private Long completedShifts;

    private Long stockCountsPerformed;

    private Long adjustmentsPerformed;

    private Long deliveriesReceived;

    private Long transfersInitiated;
}
