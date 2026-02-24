CREATE TABLE IF NOT EXISTS "агрегаты_событий_платежей" (
    "дата_и_время_записи" DateTime,
    "количество_записей" UInt64
)
ENGINE = MergeTree()
ORDER BY "дата_и_время_записи";
