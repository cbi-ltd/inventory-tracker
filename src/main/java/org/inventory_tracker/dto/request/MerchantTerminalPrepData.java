package org.inventory_tracker.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantTerminalPrepData {

    private String userProfileId;

    private String merchantName;

    private String merchantEmail;

    private String merchantType;

    private String institutionId;
}
