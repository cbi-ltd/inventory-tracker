package org.inventory_tracker.dto.response;

import java.math.BigDecimal;
import java.util.List;

import org.inventory_tracker.entity.Vehicle;
import org.inventory_tracker.enums.FuelingSettlementType;

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
public class FuelingCompanyResponse {

    private Long id;
    private String name;
    private String code;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private List<VehicleResponse> vehicles;
    private BigDecimal outstandingBalance;
    private BigDecimal creditLimit;
    private BigDecimal prepaidBalance;
    private FuelingSettlementType settlementType;
    private Boolean active;
}
