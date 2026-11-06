package org.inventory_tracker.integration.cams.service;

import java.time.Instant;
import java.util.List;
import org.inventory_tracker.integration.cams.support.WebhookSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Verifies the X-Timestamp and X-Signature headers of a CAMS webhook (contract v1).
 * The previous secret is only used while a secret is being rotated.
 */
@Component
public class CamsSignatureValidator {

    private final WebhookSignature signature;

    public CamsSignatureValidator(
            @Value("${cams.webhook.secret}") String secret,
            @Value("${cams.webhook.previous-secret:}") String previousSecret,
            @Value("${cams.webhook.tolerance-seconds:300}") long toleranceSeconds) {
        this.signature = new WebhookSignature(List.of(secret, previousSecret), toleranceSeconds);
    }

    public WebhookSignature.Result verify(byte[] rawBody, String timestamp, String signatureHeader) {
        return signature.verify(rawBody, timestamp, signatureHeader, Instant.now().getEpochSecond());
    }
}
