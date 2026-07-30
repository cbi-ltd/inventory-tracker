package org.inventory_tracker.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReverseDeliveryRequest {

    @NotBlank
    private String reason;
}
