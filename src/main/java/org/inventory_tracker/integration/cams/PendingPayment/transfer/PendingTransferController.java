package org.inventory_tracker.integration.cams.PendingPayment.transfer;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pending-transfer")
@RequiredArgsConstructor
public class PendingTransferController {

    private final PendingTransferService pendingTransferService;

    @PostMapping
    public ResponseEntity<Void> registerPendingTransfer(@Valid @RequestBody PendingTransfer pendingTransfer) {

        pendingTransferService.registerPendingTransfer(
                pendingTransfer.getVirtualAccountNumber(),
                pendingTransfer.getSaleNumber(),
                pendingTransfer.getAmount(),
                pendingTransfer.getDeviceSerial()
        );

        return ResponseEntity.ok().build();
    }
}
