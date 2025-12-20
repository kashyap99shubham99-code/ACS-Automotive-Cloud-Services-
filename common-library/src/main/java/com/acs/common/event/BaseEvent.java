package com.acs.common.event;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final Instant timestamp = Instant.now();

    public String getEventId() { return eventId; }
    public Instant getTimestamp() { return timestamp; }
}
