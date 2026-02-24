# Экзаменационная работа: ingest-service + processor-service

Проект состоит из двух Spring Boot сервисов на Java 17:

- `ingest-service` (порт `8081`)
- `processor-service` (порт `8082`)

Используемые компоненты:

- RabbitMQ (очередь `events.raw`)
- PostgreSQL (таблица `сырые_события_платежей`)
- ClickHouse (таблица `агрегаты_событий_платежей`)
- Swagger/OpenAPI в каждом сервисе

## 1. Требования

Убедитесь, что установлены:

- Java 17
- Maven Wrapper (идет в проекте, отдельная установка Maven не обязательна)
- Docker
- Docker Compose
- `curl`

Проверка версий:

```bash
java -version
docker --version
docker compose version
```

## 2. Структура проекта

```text
SenovalovE/
├── ingest-service/
└── processor-service/
    ├── docker-compose.yml
    └── sql/
        ├── postgres_init.sql
        └── clickhouse_init.sql
```

## 3. Поднять инфраструктуру (RabbitMQ + PostgreSQL + ClickHouse)

Все контейнеры поднимаются из `processor-service`:

```bash
cd processor-service
docker compose up -d
```

Проверка статуса:

```bash
docker compose ps
```

Ожидается, что запущены:

- `postgres` (`5432`)
- `clickhouse` (`8123`, `9000`)
- `rabbitmq` (`5672`, `15672`)

## 4. Запуск сервисов

Откройте два отдельных терминала.

### Терминал 1: ingest-service

```bash
cd ingest-service
./mvnw spring-boot:run
```

### Терминал 2: processor-service

```bash
cd processor-service
./mvnw spring-boot:run
```

## 5. Swagger

После старта сервисов проверьте документацию:

- Ingest Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- Processor Swagger UI: `http://localhost:8082/swagger-ui/index.html`

## 6. Полный тест функционала

### Шаг 1. Отправить события в ingest-service

Отправьте 2 события:

```bash
curl -X POST "http://localhost:8081/api/v1/events" \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "pay-001",
    "payerFullName": "Иванов Иван Иванович",
    "amount": 1500.50,
    "currency": "RUB",
    "paymentMethod": "CARD",
    "eventDate": "2026-02-24T10:00:00"
  }'

curl -X POST "http://localhost:8081/api/v1/events" \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "pay-002",
    "payerFullName": "Петров Петр Петрович",
    "amount": 2200.00,
    "currency": "RUB",
    "paymentMethod": "SBP",
    "eventDate": "2026-02-24T10:05:00"
  }'
```

Ожидаемый ответ ingest-service:

```json
{"status":"queued"}
```

### Шаг 2. Проверить запись сырых событий в PostgreSQL

`processor-service` слушает очередь `events.raw` и сохраняет события в PostgreSQL.

Проверка:

```bash
docker exec -it postgres psql -U postgres -d payments_db -c 'SELECT * FROM "сырые_события_платежей";'
```

Ожидается минимум 2 записи (`pay-001`, `pay-002`).

### Шаг 3. Вызвать подсчет и запись агрегата

Вызовите endpoint `processor-service`:

```bash
curl -X POST "http://localhost:8082/api/v1/events/count"
```

Ожидаемый ответ:

```json
{"count":2}
```

### Шаг 4. Проверить запись агрегата в ClickHouse

```bash
docker exec -it clickhouse clickhouse-client --query 'SELECT * FROM "агрегаты_событий_платежей" ORDER BY "дата_и_время_записи" DESC LIMIT 10;'
```

Ожидается новая строка с текущим временем записи и `количество_записей = 2`.

## 7. Быстрая проверка RabbitMQ

RabbitMQ Management UI:

- URL: `http://localhost:15672`
- Логин: `guest`
- Пароль: `guest`

Проверьте наличие очереди `events.raw` в разделе `Queues and Streams`.

## 8. Проверка автотестов

```bash
cd ingest-service
./mvnw test

cd ../processor-service
./mvnw test
```

## 9. Полезные команды

Остановить и удалить контейнеры:

```bash
cd processor-service
docker compose down
```

Остановить и удалить контейнеры + volumes (полная очистка данных БД):

```bash
cd processor-service
docker compose down -v
```

## 10. Что реализовано по ТЗ

- `ingest-service`
  - `POST /api/v1/events`
  - публикация в RabbitMQ очередь `events.raw`
  - Swagger
- `processor-service`
  - слушает `events.raw`
  - сохраняет события в PostgreSQL
  - `POST /api/v1/events/count`
  - считает записи в PostgreSQL
  - пишет агрегат в ClickHouse
  - Swagger
  - `docker-compose.yml`
  - SQL-скрипты для PostgreSQL и ClickHouse

