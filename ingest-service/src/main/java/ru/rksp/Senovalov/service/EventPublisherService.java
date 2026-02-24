package ru.rksp.Senovalov.service;

import ru.rksp.Senovalov.model.PaymentEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public EventPublisherService(RabbitTemplate rabbitTemplate,
                                 @Value("${app.rabbit.queue}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    public void publish(PaymentEvent event) {
        rabbitTemplate.convertAndSend(queueName, event);
    }
}
