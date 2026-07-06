package org.inventory_tracker.service;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.LoginRequest;
import org.inventory_tracker.dto.response.AuthResponse;
import org.inventory_tracker.entity.Admin;
import org.inventory_tracker.repository.AdminRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AdminRepository adminRepository;
    private final JwtService jwtService;

    public AuthResponse adminLogin(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Admin admin = adminRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        String token = jwtService.generateToken(admin);

        return AuthResponse.builder()
                .token(token)
                .email(admin.getEmail())
                .fullName(admin.getFullName())
                .role(admin.getRole().name())
                .build();
    }
}
