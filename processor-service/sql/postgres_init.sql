CREATE TABLE IF NOT EXISTS "сырые_события_платежей" (
    "идентификатор" VARCHAR(255) PRIMARY KEY,
    "фио_плательщика" VARCHAR(255) NOT NULL,
    "сумма" NUMERIC(19,2) NOT NULL,
    "валюта" VARCHAR(10) NOT NULL,
    "способ_оплаты" VARCHAR(100) NOT NULL,
    "дата_события" TIMESTAMP NOT NULL
);
