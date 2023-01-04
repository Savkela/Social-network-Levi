CREATE TABLE "confirmation_token" (
    "token" varchar,
    "confirmed_at" timestamp,
    "id_user" bigint,
    PRIMARY KEY("token")
);