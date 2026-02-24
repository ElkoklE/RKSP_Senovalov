package ru.rksp.Senovalov.listener;

import ru.rksp.Senovalov.model.PaymentEvent;
import ru.rksp.Senovalov.service.EventProcessorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    private final EventProcessorService eventProcessorService;

    public EventListener(EventProcessorService eventProcessorService) {
        this.eventProcessorService = eventProcessorService;
    }

    @RabbitListener(queues = "${app.rabbit.queue}")
    public void consumeRawEvent(PaymentEvent event) {
        eventProcessorService.saveRawEvent(event);
    }
}
