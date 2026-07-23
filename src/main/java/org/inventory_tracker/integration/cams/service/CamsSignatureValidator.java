package org.inventory_tracker.integration.cams.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class CamsSignatureValidator {

    @Value("${cams.webhook.secret}")
    private String webhookSecret;

    public boolean isValid(
            String payload,
            String signature) {

        try {

            String expected =
                    generateSignature(payload);

            return expected.equalsIgnoreCase(signature);

        } catch (Exception ex) {

            return false;

        }

    }

    private String generateSignature(String payload)
            throws Exception {

        Mac mac =
                Mac.getInstance("HmacSHA256");

        SecretKeySpec key =
                new SecretKeySpec(

                        webhookSecret.getBytes(StandardCharsets.UTF_8),

                        "HmacSHA256"

                );

        mac.init(key);

        byte[] hash =
                mac.doFinal(

                        payload.getBytes(StandardCharsets.UTF_8)

                );

        return HexFormat.of().formatHex(hash);

    }

}
