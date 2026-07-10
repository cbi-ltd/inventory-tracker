package org.inventory_tracker.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.inventory_tracker.enums.Shift;

public final class ShiftUtil {

    private ShiftUtil() {}

    // public static Shift currentShift() {
    //     return currentShift(ZoneId.systemDefault());
    // }

    public static Shift currentShift() {

        LocalTime now = LocalTime.now();

        if (now.isBefore(LocalTime.of(6, 0))) {
            return Shift.OVERNIGHT;
        }

        if (now.isBefore(LocalTime.of(14, 0))) {
            return Shift.MORNING;
        }

        if (now.isBefore(LocalTime.of(22, 0))) {
            return Shift.AFTERNOON;
        }

        return Shift.NIGHT;
    }

    public static Shift currentShift(ZoneId zoneId) {

        LocalTime now = LocalTime.now(zoneId);

        if (now.isBefore(LocalTime.of(6, 0))) {
            return Shift.OVERNIGHT;
        }

        if (now.isBefore(LocalTime.of(14, 0))) {
            return Shift.MORNING;
        }

        if (now.isBefore(LocalTime.of(22, 0))) {
            return Shift.AFTERNOON;
        }

        return Shift.NIGHT;
    }

    public static LocalDate businessDate(ZoneId zoneId) {
        return LocalDate.now(zoneId);
    }
}
