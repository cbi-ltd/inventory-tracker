package org.inventory_tracker.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PaymentMethod {
    CASH,
    CARD,
    TRANSFER,
    MIXED;

    @JsonCreator
    public static PaymentMethod fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return PaymentMethod.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown payment method: " + value);
        }
    }
}
