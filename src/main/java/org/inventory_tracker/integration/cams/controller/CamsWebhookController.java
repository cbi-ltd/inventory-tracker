package org.inventory_tracker.integration.cams.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory_tracker.integration.cams.dto.CamsPaymentNotification;
import org.inventory_tracker.integration.cams.service.CamsSignatureValidator;
import org.inventory_tracker.integration.cams.service.CamsWebhookService;
import org.inventory_tracker.dto.common.ApiSuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/integrations/cams")
public class CamsWebhookController {

    private final ObjectMapper objectMapper;
    private final CamsSignatureValidator signatureValidator;
    private final CamsWebhookService camsWebhookService;


    @PostMapping("/payment-notification")
    public ResponseEntity<ApiSuccessResponse<String>> receivePaymentNotification(@RequestBody String payload,
                    @RequestHeader(value = "X-Signature", required = false) String signature) {

        try {
            if (!signatureValidator.isValid(payload, signature)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                                    "Invalid webhook signature", 0, null));
            }

            CamsPaymentNotification notification = objectMapper.readValue(payload, CamsPaymentNotification.class);
            camsWebhookService.processTransferPayment(notification);

            return ResponseEntity.ok(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.OK.value(), "Payment notification processed successfully", 1, notification.getRequestReference()));
        } 
        catch (Exception ex) {
            log.error("Error processing CAMS payment notification", ex);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiSuccessResponse<>(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unable to process payment notification", 0, null));
        }
    }
}
