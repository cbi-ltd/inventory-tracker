package org.inventory_tracker.security;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.inventory_tracker.dto.response.CamsProfileData;
import org.inventory_tracker.dto.response.CamsProfileResponse;
import org.springframework.http.*;


@Service
@RequiredArgsConstructor
public class CamsAuthenticationService {

    private final RestClient camsRestClient;

    public CamsProfileData authenticate(String bearerToken) {

        if (bearerToken == null || bearerToken.isBlank()) {
            throw new InvalidCamsAuthenticationException(
                    "CAMS authentication token is required.");
        }

        CamsProfileResponse response;

        try {

            response = camsRestClient.get()
                    .uri("/cbi-request-api/v1/user/get-profile")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            bearerToken)
                    .retrieve()
                    .body(CamsProfileResponse.class);

        } catch (HttpClientErrorException.Unauthorized ex) {

            throw new InvalidCamsAuthenticationException(
                    "Invalid CAMS authentication.");

        } catch (HttpClientErrorException.Forbidden ex) {

            throw new InvalidCamsAuthenticationException(
                    "CAMS authentication was denied.");

        } catch (RestClientException ex) {

            throw new InvalidCamsAuthenticationException(
                    "Unable to verify CAMS authentication.",
                    ex);
        }

        if (response == null) {

            throw new InvalidCamsAuthenticationException(
                    "Empty response received from CAMS.");
        }

        if (!response.isSuccess()) {

            throw new InvalidCamsAuthenticationException(
                    response.getMessage() != null
                            ? response.getMessage()
                            : "CAMS authentication failed.");
        }

        CamsProfileData profile = response.getData();

        if (profile == null) {

            throw new InvalidCamsAuthenticationException(
                    "CAMS profile was not returned.");
        }

        validateProfile(profile);

        return profile;
    }


    private void validateProfile(CamsProfileData profile) {

        if (!"ACTIVE".equalsIgnoreCase(profile.getStatus())) {

            throw new InvalidCamsAuthenticationException(
                    "CAMS user account is not active.");
        }

        if (!"APPROVED".equalsIgnoreCase(profile.getKycStatus())) {

            throw new InvalidCamsAuthenticationException(
                    "CAMS user KYC is not approved.");
        }

        if (profile.getUserId() == null
                || profile.getUserId().isBlank()) {

            throw new InvalidCamsAuthenticationException(
                    "CAMS profile does not contain a user ID.");
        }

        if (profile.getProfileType() == null
                || profile.getProfileType().isBlank()) {

            throw new InvalidCamsAuthenticationException(
                    "CAMS profile type is missing.");
        }
    }
}
