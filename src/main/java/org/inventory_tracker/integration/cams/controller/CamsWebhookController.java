package org.inventory_tracker.integration.cams.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory_tracker.integration.cams.dto.CamsPaymentNotification;
import org.inventory_tracker.integration.cams.dto.CamsWebhookResponse;
import org.inventory_tracker.integration.cams.dto.CardPaymentNotification;
import org.inventory_tracker.integration.cams.service.CamsSignatureValidator;
import org.inventory_tracker.integration.cams.service.CamsWebhookService;
import org.inventory_tracker.integration.cams.support.WebhookSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Receives the signed notices CAMS sends (contract v1). The reply tells CAMS what happened:
 * 200 with status processed, duplicate or ignored (CAMS stops retrying), 401 for a bad signature or timestamp,
 * 400 for a body that cannot be read, and 500 only for a real failure (CAMS retries).
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/integrations/cams")
public class CamsWebhookController {

    private final ObjectMapper objectMapper;
    private final CamsSignatureValidator signatureValidator;
    private final CamsWebhookService camsWebhookService;

    @PostMapping("/transfer")
    public ResponseEntity<CamsWebhookResponse> receivePaymentNotification(
            @RequestBody byte[] body,
            @RequestHeader(value = "X-Timestamp", required = false) String timestamp,
            @RequestHeader(value = "X-Signature", required = false) String signature) {

        ResponseEntity<CamsWebhookResponse> rejected = rejectIfNotAuthentic(body, timestamp, signature);
        if (rejected != null) {
            return rejected;
        }
        CamsPaymentNotification notification;
        try {
            notification = objectMapper.readValue(body, CamsPaymentNotification.class);
        } catch (IOException ex) {
            log.warn("CAMS transfer notice could not be read: {}", ex.getMessage());
            return malformed();
        }
        return ResponseEntity.ok(camsWebhookService.handleTransfer(notification, new String(body, StandardCharsets.UTF_8)));
    }

    @PostMapping("/card")
    public ResponseEntity<CamsWebhookResponse> receiveCardPayment(
            @RequestBody byte[] body,
            @RequestHeader(value = "X-Timestamp", required = false) String timestamp,
            @RequestHeader(value = "X-Signature", required = false) String signature) {

        ResponseEntity<CamsWebhookResponse> rejected = rejectIfNotAuthentic(body, timestamp, signature);
        if (rejected != null) {
            return rejected;
        }
        CardPaymentNotification notification;
        try {
            notification = objectMapper.readValue(body, CardPaymentNotification.class);
        } catch (IOException ex) {
            log.warn("CAMS card notice could not be read: {}", ex.getMessage());
            return malformed();
        }
        return ResponseEntity.ok(camsWebhookService.handleCard(notification, new String(body, StandardCharsets.UTF_8)));
    }

    /** Returns the 401 reply when the signature or timestamp is not acceptable, or null when the notice is genuine. */
    private ResponseEntity<CamsWebhookResponse> rejectIfNotAuthentic(byte[] body, String timestamp, String signature) {
        WebhookSignature.Result result = signatureValidator.verify(body, timestamp, signature);
        if (result == WebhookSignature.Result.VALID) {
            return null;
        }
        log.warn("CAMS webhook rejected: {}", result);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new CamsWebhookResponse("rejected", "Invalid signature or timestamp"));
    }

    private ResponseEntity<CamsWebhookResponse> malformed() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CamsWebhookResponse("rejected", "Malformed notification"));
    }
}
