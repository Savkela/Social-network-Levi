--liquibase formatted sql

--changeset postgres:1

CREATE TABLE "role" (
    "id" bigint generated always as identity primary key,
    "name" varchar
);
-- rollback drop table applicationinfo