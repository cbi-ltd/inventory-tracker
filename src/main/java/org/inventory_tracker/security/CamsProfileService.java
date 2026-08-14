package org.inventory_tracker.security;

import org.inventory_tracker.dto.response.CamsProfileResponse;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import org.springframework.http.*;

@Service
@RequiredArgsConstructor
public class CamsProfileService {

    private final RestClient restClient;

    public CamsProfileResponse getProfile(String bearerToken) {

        return restClient.get()
                .uri("/cbi-request-api/v1/user/get-profile")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .body(CamsProfileResponse.class);
    }
}
