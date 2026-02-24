package ru.rksp.Senovalov.repository;

import ru.rksp.Senovalov.model.PaymentEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class EventRepository {
    private static final DateTimeFormatter CLICKHOUSE_DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String INSERT_RAW_SQL = """
            INSERT INTO \"сырые_события_платежей\" (
                \"идентификатор\", \"фио_плательщика\", \"сумма\", \"валюта\", \"способ_оплаты\", \"дата_события\"
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String COUNT_RAW_SQL = "SELECT COUNT(*) FROM \"сырые_события_платежей\"";

    private final JdbcTemplate postgresJdbcTemplate;
    private final JdbcTemplate clickhouseJdbcTemplate;

    public EventRepository(@Qualifier("postgresJdbcTemplate") JdbcTemplate postgresJdbcTemplate,
                           @Qualifier("clickhouseJdbcTemplate") JdbcTemplate clickhouseJdbcTemplate) {
        this.postgresJdbcTemplate = postgresJdbcTemplate;
        this.clickhouseJdbcTemplate = clickhouseJdbcTemplate;
    }

    public void insertRawEvent(PaymentEvent event) {
        postgresJdbcTemplate.update(
                INSERT_RAW_SQL,
                event.identifier(),
                event.payerFullName(),
                event.amount(),
                event.currency(),
                event.paymentMethod(),
                Timestamp.valueOf(event.eventDate())
        );
    }

    public long countRawEvents() {
        Long count = postgresJdbcTemplate.queryForObject(COUNT_RAW_SQL, Long.class);
        return count == null ? 0L : count;
    }

    public void insertAggregate(long count) {
        String now = LocalDateTime.now().withNano(0).format(CLICKHOUSE_DT_FORMATTER);
        String sql = """
                INSERT INTO \"агрегаты_событий_платежей\" (\"дата_и_время_записи\", \"количество_записей\")
                VALUES (toDateTime('%s'), %d)
                """.formatted(now, count);
        clickhouseJdbcTemplate.execute(sql);
    }
}
