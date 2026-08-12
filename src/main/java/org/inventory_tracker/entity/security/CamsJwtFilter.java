package org.inventory_tracker.entity.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.inventory_tracker.service.MerchantService;
import org.inventory_tracker.entity.Merchant;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CamsJwtFilter extends OncePerRequestFilter {

    private final CamsJwtService camsJwtService;
    private final MerchantService merchantService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                MerchantPrincipal merchantPrincipal = camsJwtService.parse(token);
                Merchant merchant = merchantService.getCurrentMerchant(merchantPrincipal.getMerchantId());
                MerchantContext.setCurrentMerchant(merchant);
            }

            filterChain.doFilter(request, response);
        }
        catch (Exception ex) {
            log.error("CAMS authentication failed", ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid CAMS authentication");
            response.setContentType("application/json");
        } 
        finally {
            MerchantContext.clear();
        }
    }
}
