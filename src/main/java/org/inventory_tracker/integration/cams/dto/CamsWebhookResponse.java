package org.inventory_tracker.integration.cams.dto;

/** The reply to CAMS: {"status": "processed" | "duplicate" | "ignored", "message": "..."}. */
public record CamsWebhookResponse(String status, String message) {

    public static CamsWebhookResponse processed(String message) {
        return new CamsWebhookResponse("processed", message);
    }

    public static CamsWebhookResponse duplicate(String message) {
        return new CamsWebhookResponse("duplicate", message);
    }

    public static CamsWebhookResponse ignored(String message) {
        return new CamsWebhookResponse("ignored", message);
    }
}
