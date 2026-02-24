package ru.rksp.Senovalov.api;

import ru.rksp.Senovalov.dto.CountResponse;
import ru.rksp.Senovalov.service.EventProcessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Events Processor API")
public class EventController {

    private final EventProcessorService eventProcessorService;

    public EventController(EventProcessorService eventProcessorService) {
        this.eventProcessorService = eventProcessorService;
    }

    @PostMapping("/count")
    @Operation(summary = "Count records in PostgreSQL and write count to ClickHouse")
    public ResponseEntity<CountResponse> countEvents() {
        long count = eventProcessorService.countAndWriteAggregate();
        return ResponseEntity.ok(new CountResponse(count));
    }
}
