package org.inventory_tracker.controller;

import lombok.RequiredArgsConstructor;
import org.inventory_tracker.dto.request.CreateTransactionRequest;
import org.inventory_tracker.dto.response.SaleTransactionResponse;
import org.inventory_tracker.service.SaleTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sale-transactions")
@RequiredArgsConstructor
public class SaleTransactionController {

    private final SaleTransactionService
            saleTransactionService;

    @PostMapping
    public ResponseEntity<SaleTransactionResponse>
    create(
            @RequestBody
            CreateTransactionRequest request
    ) {

        return new ResponseEntity<>(
                saleTransactionService.create(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<SaleTransactionResponse>>
    getAll() {

        return ResponseEntity.ok(
                saleTransactionService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleTransactionResponse>
    getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                saleTransactionService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleTransactionResponse>
    update(
            @PathVariable Long id,
            @RequestBody
            CreateTransactionRequest request
    ) {

        return ResponseEntity.ok(
                saleTransactionService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        saleTransactionService.delete(id);

        return ResponseEntity.noContent().build();
    }
}