package org.inventory_tracker.integration.cams.support;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Checks the signature on a CAMS webhook (contract v1). It needs only the JDK.
 *
 * The signed text is "{X-Timestamp}.{raw body bytes}" with HMAC-SHA256, written as lowercase hex.
 * The timestamp is when CAMS sent that attempt, so a retry hours later is signed afresh and still passes.
 */
public final class WebhookSignature {

    public enum Result { VALID, MISSING_HEADERS, STALE_TIMESTAMP, BAD_SIGNATURE }

    private final List<String> secrets = new ArrayList<>();
    private final long toleranceSeconds;

    /** Blank secrets are dropped: an empty secret must never be able to validate anything. */
    public WebhookSignature(List<String> secrets, long toleranceSeconds) {
        for (String s : secrets) {
            if (s != null && !s.isBlank()) {
                this.secrets.add(s);
            }
        }
        this.toleranceSeconds = toleranceSeconds;
    }

    public Result verify(byte[] rawBody, String timestampHeader, String signatureHeader, long nowEpochSeconds) {
        if (rawBody == null || isBlank(timestampHeader) || isBlank(signatureHeader)) {
            return Result.MISSING_HEADERS;
        }
        String timestamp = timestampHeader.trim();
        long sentAt;
        try {
            sentAt = Long.parseLong(timestamp);
        } catch (NumberFormatException ex) {
            return Result.STALE_TIMESTAMP;
        }
        if (Math.abs(nowEpochSeconds - sentAt) > toleranceSeconds) {
            return Result.STALE_TIMESTAMP;
        }
        byte[] provided = signatureHeader.trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII);
        boolean matched = false;
        for (String secret : secrets) {
            byte[] expected = sign(secret, timestamp, rawBody).getBytes(StandardCharsets.US_ASCII);
            // Constant-time comparison, and every configured secret is always checked.
            matched |= MessageDigest.isEqual(expected, provided);
        }
        return matched ? Result.VALID : Result.BAD_SIGNATURE;
    }

    /** The X-Signature value for a body: lowercase hex HMAC-SHA256 of "{timestamp}.{body}". */
    public static String sign(String secret, String timestamp, byte[] body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            mac.update(timestamp.getBytes(StandardCharsets.UTF_8));
            mac.update((byte) '.');
            mac.update(body);
            return HexFormat.of().formatHex(mac.doFinal());
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("HmacSHA256 is not available", ex);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
