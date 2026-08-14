package org.inventory_tracker.security;

import lombok.*;
import org.inventory_tracker.dto.response.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import org.springframework.stereotype.Component;




@Component
@RequiredArgsConstructor
public class CamsAuthenticationFilter extends OncePerRequestFilter {
    private final CamsAuthenticationService camsAuthenticationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    """
                    {
                        "status": 401,
                        "message": "CAMS authentication token is required."
                    }
                    """
            );

            return;
        }

        try {

            CamsProfileData profile =camsAuthenticationService.authenticate(authorization);

            MerchantPrincipal principal = MerchantPrincipal.builder()
                            .merchantId(profile.getUserId())
                            .institutionId(profile.getInstitutionId())
                            .role(profile.getProfileType())
                            .email(profile.getEmail())
                            .merchantName(profile.getBusinessName())
                            .build();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            Collections.emptyList()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (InvalidCamsAuthenticationException ex) {

            SecurityContextHolder.clearContext();

            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            response.getWriter().write(
                    """
                    {
                        "status": 401,
                        "message": "Invalid CAMS authentication."
                    }
                    """
            );
        }
    }
}
