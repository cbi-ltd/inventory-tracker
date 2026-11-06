package org.inventory_tracker.integration.cams.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.inventory_tracker.integration.cams.support.WebhookSignature.Result;
import org.junit.jupiter.api.Test;

class WebhookSignatureTest {

    private static final String SECRET = "test-secret-do-not-use-in-production";
    private static final long NOW = 1_793_797_200L;
    private static final byte[] BODY = "{\"eventId\":\"evt_1\",\"amount\":5000.00}".getBytes(StandardCharsets.UTF_8);

    private static WebhookSignature verifier(String... secrets) {
        return new WebhookSignature(Arrays.asList(secrets), 300);
    }

    private static String signedAt(long ts, byte[] body, String secret) {
        return WebhookSignature.sign(secret, Long.toString(ts), body);
    }

    /** The test vector published in the webhook contract. The Go sender reproduces the same value. */
    @Test
    void reproducesTheContractTestVector() {
        String body = "{\"eventId\":\"evt_test_0001\",\"eventType\":\"payment.success\",\"version\":\"1\",\"amount\":5000.00}";
        assertEquals("3a34ddbefabc5e2ce03e0845cfe0c06ec93cd773e2891b0705d9c4549013b0ed",
                WebhookSignature.sign(SECRET, "1793797200", body.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void acceptsAGenuineNotice() {
        assertEquals(Result.VALID, verifier(SECRET).verify(BODY, Long.toString(NOW), signedAt(NOW, BODY, SECRET), NOW));
    }

    @Test
    void rejectsAnAlteredBody() {
        byte[] other = "{\"eventId\":\"evt_1\",\"amount\":9999.00}".getBytes(StandardCharsets.UTF_8);
        assertEquals(Result.BAD_SIGNATURE, verifier(SECRET).verify(other, Long.toString(NOW), signedAt(NOW, BODY, SECRET), NOW));
    }

    @Test
    void rejectsATimestampThatWasNotSigned() {
        String signedForNow = signedAt(NOW, BODY, SECRET);
        assertEquals(Result.BAD_SIGNATURE, verifier(SECRET).verify(BODY, Long.toString(NOW + 5), signedForNow, NOW));
    }

    @Test
    void rejectsTheWrongSecret() {
        assertEquals(Result.BAD_SIGNATURE, verifier(SECRET).verify(BODY, Long.toString(NOW), signedAt(NOW, BODY, "another-secret"), NOW));
    }

    @Test
    void rejectsMissingHeaders() {
        WebhookSignature v = verifier(SECRET);
        String sig = signedAt(NOW, BODY, SECRET);
        assertEquals(Result.MISSING_HEADERS, v.verify(BODY, null, sig, NOW));
        assertEquals(Result.MISSING_HEADERS, v.verify(BODY, "  ", sig, NOW));
        assertEquals(Result.MISSING_HEADERS, v.verify(BODY, Long.toString(NOW), null, NOW));
        assertEquals(Result.MISSING_HEADERS, v.verify(BODY, Long.toString(NOW), "", NOW));
        assertEquals(Result.MISSING_HEADERS, v.verify(null, Long.toString(NOW), sig, NOW));
    }

    @Test
    void rejectsAStaleOrFutureOrNonNumericTimestamp() {
        WebhookSignature v = verifier(SECRET);
        long old = NOW - 301;
        long future = NOW + 301;
        assertEquals(Result.STALE_TIMESTAMP, v.verify(BODY, Long.toString(old), signedAt(old, BODY, SECRET), NOW));
        assertEquals(Result.STALE_TIMESTAMP, v.verify(BODY, Long.toString(future), signedAt(future, BODY, SECRET), NOW));
        assertEquals(Result.STALE_TIMESTAMP, v.verify(BODY, "yesterday", "abc", NOW));
    }

    @Test
    void toleranceBoundaryIsFiveMinutesInclusive() {
        WebhookSignature v = verifier(SECRET);
        long edge = NOW - 300;
        assertEquals(Result.VALID, v.verify(BODY, Long.toString(edge), signedAt(edge, BODY, SECRET), NOW));
    }

    @Test
    void acceptsThePreviousSecretDuringRotation() {
        WebhookSignature v = verifier("new-secret", "old-secret");
        assertEquals(Result.VALID, v.verify(BODY, Long.toString(NOW), signedAt(NOW, BODY, "old-secret"), NOW));
        assertEquals(Result.VALID, v.verify(BODY, Long.toString(NOW), signedAt(NOW, BODY, "new-secret"), NOW));
        assertEquals(Result.BAD_SIGNATURE, v.verify(BODY, Long.toString(NOW), signedAt(NOW, BODY, "third-secret"), NOW));
    }

    @Test
    void blankSecretsCanNeverValidateAnything() {
        WebhookSignature v = new WebhookSignature(Arrays.asList("", "   ", null), 300);
        assertEquals(Result.BAD_SIGNATURE, v.verify(BODY, Long.toString(NOW), signedAt(NOW, BODY, SECRET), NOW));
        assertEquals(Result.BAD_SIGNATURE, v.verify(BODY, Long.toString(NOW), "00", NOW));
    }

    @Test
    void ignoresUppercaseAndSurroundingSpacesInTheHeaders() {
        String sig = signedAt(NOW, BODY, SECRET).toUpperCase();
        assertEquals(Result.VALID, verifier(SECRET).verify(BODY, " " + NOW + " ", " " + sig + " ", NOW));
    }

    @Test
    void rejectsASignatureOfTheWrongLengthOrNotHex() {
        WebhookSignature v = verifier(SECRET);
        String real = signedAt(NOW, BODY, SECRET);
        assertEquals(Result.BAD_SIGNATURE, v.verify(BODY, Long.toString(NOW), real.substring(0, 63), NOW));
        assertEquals(Result.BAD_SIGNATURE, v.verify(BODY, Long.toString(NOW), real + "00", NOW));
        assertEquals(Result.BAD_SIGNATURE, v.verify(BODY, Long.toString(NOW), "not-hex-at-all", NOW));
    }

    @Test
    void signsTheExactBytesNotAReEncodedString() {
        byte[] utf8 = "{\"payerName\":\"\u00D1andi \u00D6kezie\"}".getBytes(StandardCharsets.UTF_8);
        byte[] latin1 = "{\"payerName\":\"\u00D1andi \u00D6kezie\"}".getBytes(StandardCharsets.ISO_8859_1);
        String sigOfUtf8 = signedAt(NOW, utf8, SECRET);
        assertEquals(Result.VALID, verifier(SECRET).verify(utf8, Long.toString(NOW), sigOfUtf8, NOW));
        assertEquals(Result.BAD_SIGNATURE, verifier(SECRET).verify(latin1, Long.toString(NOW), sigOfUtf8, NOW));
    }

    @Test
    void anEmptyListOfSecretsFailsClosed() {
        assertEquals(Result.BAD_SIGNATURE, new WebhookSignature(List.of(), 300)
                .verify(BODY, Long.toString(NOW), signedAt(NOW, BODY, SECRET), NOW));
    }
}
