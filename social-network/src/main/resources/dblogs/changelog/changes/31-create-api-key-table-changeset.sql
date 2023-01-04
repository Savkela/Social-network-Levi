CREATE TABLE "api_key" (
    "id" bigint generated always as identity primary key,
    "hash_value" varchar,
    "id_user" bigint
);