package org.inventory_tracker.integration.cams.support;

/** The duplicate key of a CAMS notice. It needs only the JDK. */
public final class EventIds {

    private EventIds() { }

    /** The notice's eventId, or "evt_" plus its requestReference, or null when it has neither. */
    public static String resolve(String eventId, String requestReference) {
        if (eventId != null && !eventId.isBlank()) {
            return eventId.trim();
        }
        if (requestReference != null && !requestReference.isBlank()) {
            return "evt_" + requestReference.trim();
        }
        return null;
    }
}
