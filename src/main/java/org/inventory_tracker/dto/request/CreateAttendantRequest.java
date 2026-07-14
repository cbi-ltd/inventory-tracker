package org.inventory_tracker.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAttendantRequest {

    @NotBlank
    private String username;

    private String pin;

    @NotBlank
    private String fullName;

    @NotNull
    private Long stationId;
}
