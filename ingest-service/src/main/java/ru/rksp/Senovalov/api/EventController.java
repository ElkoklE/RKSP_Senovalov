package ru.rksp.Senovalov.api;

import ru.rksp.Senovalov.model.PaymentEvent;
import ru.rksp.Senovalov.service.EventPublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Events Ingest API")
public class EventController {

    private final EventPublisherService publisherService;

    public EventController(EventPublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping
    @Operation(summary = "Accept event and send it to RabbitMQ queue events.raw")
    public ResponseEntity<Map<String, String>> ingestEvent(@Valid @RequestBody PaymentEvent event) {
        publisherService.publish(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("status", "queued"));
    }
}
