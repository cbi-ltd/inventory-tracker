package org.inventory_tracker.controller;


import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.LoginRequest;
import org.inventory_tracker.dto.response.AuthResponse;
import org.inventory_tracker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }
}
