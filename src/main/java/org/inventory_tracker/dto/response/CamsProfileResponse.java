package org.inventory_tracker.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamsProfileResponse {

    private String code;

    private CamsProfileData data;

    private String message;

    private boolean success;
}
