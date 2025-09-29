package com.slimczes.orders.domain.port.messaging;

import java.util.List;

import com.slimczes.orders.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}
