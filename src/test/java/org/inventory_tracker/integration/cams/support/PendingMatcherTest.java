package org.inventory_tracker.integration.cams.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.inventory_tracker.integration.cams.support.PendingMatcher.Candidate;
import org.inventory_tracker.integration.cams.support.PendingMatcher.Decision;
import org.inventory_tracker.integration.cams.support.PendingMatcher.Kind;
import org.junit.jupiter.api.Test;

class PendingMatcherTest {

    private static Candidate<String> open(String sale, String amount) {
        return new Candidate<>(sale, amount == null ? null : new BigDecimal(amount));
    }

    @Test
    void nothingOpenMeansNone() {
        assertEquals(Kind.NONE, PendingMatcher.decide(List.<Candidate<String>>of(), new BigDecimal("5000")).kind());
        assertEquals(Kind.NONE, PendingMatcher.<String>decide(null, new BigDecimal("5000")).kind());
        assertNull(PendingMatcher.<String>decide(null, new BigDecimal("5000")).pending());
    }

    @Test
    void anExactAmountMatches() {
        Decision<String> d = PendingMatcher.decide(List.of(open("S1", "5000.00")), new BigDecimal("5000.00"));
        assertEquals(Kind.MATCH, d.kind());
        assertEquals("S1", d.pending());
    }

    @Test
    void scaleDoesNotMatter() {
        assertEquals(Kind.MATCH, PendingMatcher.decide(List.of(open("S1", "5000.00")), new BigDecimal("5000")).kind());
        assertEquals(Kind.MATCH, PendingMatcher.decide(List.of(open("S1", "5000")), new BigDecimal("5000.0")).kind());
    }

    @Test
    void aDifferentAmountIsAMismatchOnTheOldestOpenPayment() {
        Decision<String> d = PendingMatcher.decide(List.of(open("S1", "5000"), open("S2", "7000")), new BigDecimal("4000"));
        assertEquals(Kind.AMOUNT_MISMATCH, d.kind());
        assertEquals("S1", d.pending());
    }

    @Test
    void withSeveralOpenPaymentsTheOldestExactAmountWins() {
        Decision<String> d = PendingMatcher.decide(List.of(open("S1", "5000"), open("S2", "7000"), open("S3", "7000")), new BigDecimal("7000"));
        assertEquals(Kind.MATCH, d.kind());
        assertEquals("S2", d.pending());
    }

    @Test
    void aPaymentWithoutAnAmountNeverMatches() {
        Decision<String> d = PendingMatcher.decide(List.of(open("S1", null)), new BigDecimal("5000"));
        assertEquals(Kind.AMOUNT_MISMATCH, d.kind());
    }

    @Test
    void aNoticeWithoutAnAmountIsRefused() {
        assertThrows(NullPointerException.class, () -> PendingMatcher.decide(List.of(open("S1", "5000")), null));
    }
}
