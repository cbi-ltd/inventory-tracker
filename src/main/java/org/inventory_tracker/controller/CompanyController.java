package org.inventory_tracker.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateCompanyRequest;
import org.inventory_tracker.dto.response.CompanyResponse;
import org.inventory_tracker.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAll() {
        List<CompanyResponse> responses = companyService.getAll();
        return ResponseEntity.ok(responses);
    }
}
