--liquibase formatted sql

--changeset postgres:1

CREATE TABLE "user" (
    "id" bigint generated always as identity primary key,
    "name" varchar,
    "surname" varchar,
    "email" varchar,
    "username" varchar,
    "password" varchar,
    "last_password_reset_date" timestamp,
    "status" varchar
);
-- rollback drop table applicationinfo