package org.inventory_tracker.integration.cams.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CamsWebhookEventRepository extends JpaRepository<CamsWebhookEvent, Long> {

    boolean existsByEventId(String eventId);
}
