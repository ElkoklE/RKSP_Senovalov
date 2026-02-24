package ru.rksp.Senovalov.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentEvent(
        String identifier,
        String payerFullName,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime eventDate
) {
}
