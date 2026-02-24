package ru.rksp.Senovalov.service;

import ru.rksp.Senovalov.model.PaymentEvent;
import ru.rksp.Senovalov.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class EventProcessorService {

    private final EventRepository eventRepository;

    public EventProcessorService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void saveRawEvent(PaymentEvent event) {
        eventRepository.insertRawEvent(event);
    }

    public long countAndWriteAggregate() {
        long count = eventRepository.countRawEvents();
        eventRepository.insertAggregate(count);
        return count;
    }
}
