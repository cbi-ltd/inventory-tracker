package org.inventory_tracker.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CamsProfileData {

    private String fullName;

    private String firstName;

    private String lastName;

    private String middleName;

    private String gender;

    private String dateOfBirth;

    private String city;

    private String state;

    private String businessName;

    private String businessType;

    private String email;

    private String phoneNumber;

    private String bvn;

    private String address;

    private String tin;

    private String accountNumber;

    private String userId;

    private String status;

    private String profileType;

    private String lastLogin;

    private String createdAt;

    private String kycStatus;

    private List<CamsKycDocument> kycDocuments;

    private Object directorsInfo;

    private String profilePhotoUrl;

    private String institutionId;
}
