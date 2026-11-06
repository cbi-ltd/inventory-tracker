package org.inventory_tracker.integration.cams.support;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Decides which open payment on a terminal a CAMS notice belongs to. It needs only the JDK.
 *
 * The caller passes the open (not expired) payments of ONE terminal, oldest first. A payment matches when its
 * amount is exactly equal (5000 and 5000.00 are equal). If something is open on the terminal but no amount
 * matches, the notice is an amount mismatch: it is recorded for review and never completes a sale.
 */
public final class PendingMatcher {

    public enum Kind { MATCH, AMOUNT_MISMATCH, NONE }

    public record Candidate<T>(T pending, BigDecimal amount) { }

    public record Decision<T>(Kind kind, T pending) { }

    private PendingMatcher() { }

    public static <T> Decision<T> decide(List<Candidate<T>> openOldestFirst, BigDecimal paid) {
        Objects.requireNonNull(paid, "paid");
        if (openOldestFirst == null || openOldestFirst.isEmpty()) {
            return new Decision<>(Kind.NONE, null);
        }
        for (Candidate<T> candidate : openOldestFirst) {
            if (candidate.amount() != null && candidate.amount().compareTo(paid) == 0) {
                return new Decision<>(Kind.MATCH, candidate.pending());
            }
        }
        return new Decision<>(Kind.AMOUNT_MISMATCH, openOldestFirst.get(0).pending());
    }
}
