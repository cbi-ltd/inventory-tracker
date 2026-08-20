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

        if (isPosEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(
                    MediaType.APPLICATION_JSON_VALUE);

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
            MerchantPrincipal principal = camsAuthenticationService.authenticateUser(authorization);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal,  null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } 
        catch (InvalidCamsAuthenticationException ex) {
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

        private boolean isPosEndpoint(HttpServletRequest request) {

                String method = request.getMethod();
                String uri = request.getRequestURI();

                return
                        ("GET".equalsIgnoreCase(method)
                                && uri.equals("/api/v1/terminals/session"))

                        ||

                        ("GET".equalsIgnoreCase(method)
                                && uri.equals("/api/v1/pump-audits/shift-summary"))

                        ||

                        ("GET".equalsIgnoreCase(method)
                                && uri.equals("/api/v1/station-inventories/price"))

                        ||

                        ("POST".equalsIgnoreCase(method)
                                && uri.equals("/api/v1/sales"))

                        ||

                        ("PUT".equalsIgnoreCase(method)
                                && uri.equals("/api/v1/pump-audits/close"));

        }
}