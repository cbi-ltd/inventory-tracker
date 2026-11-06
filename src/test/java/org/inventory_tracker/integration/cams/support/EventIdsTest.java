package org.inventory_tracker.integration.cams.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class EventIdsTest {

    @Test
    void theEventIdIsPreferred() {
        assertEquals("evt_A", EventIds.resolve(" evt_A ", "REF1"));
    }

    @Test
    void theRequestReferenceIsTheFallback() {
        assertEquals("evt_REF1", EventIds.resolve(null, " REF1 "));
        assertEquals("evt_REF1", EventIds.resolve("  ", "REF1"));
    }

    @Test
    void withNeitherThereIsNoKey() {
        assertNull(EventIds.resolve(null, null));
        assertNull(EventIds.resolve("", " "));
    }
}
