package org.inventory_tracker.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamsKycDocument {

    private String type;

    private String status;

    private String comment;

    private String fileUrl;

    private String documentNumber;
}
