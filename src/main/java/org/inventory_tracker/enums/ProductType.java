package org.inventory_tracker.enums;

import java.util.Arrays;

public enum ProductType {
    PETROL("PMS", "Premium Motor Spirit"),
    DIESEL("AGO", "Automotive Gas Oil"),
    COOKING_GAS("LPG", "Liquefied Petroleum Gas"),
    JET_FUEL("ATK", "Aviation Turbine Kerosene"),
    KEROSENE("KERO", "Kerosene"),
    ENGINE_OIL("ENGO", "Engine Oil"),
    LUBRICANTS("LUB", "Lubricants"),
    COOLANT("COOL", "Coolant"),
    TYRE("TYRE", "Tyre"),
    WATER("WATER", "Water");

    private final String code;
    private final String description;

    ProductType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ProductType fromValue(String value){
        return Arrays.stream(values())
            .filter(type -> type.name().equalsIgnoreCase(value) 
                || type.code.equalsIgnoreCase(value)
                || type.description.equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown product type: " + value));
    }
}
