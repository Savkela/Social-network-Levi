--liquibase formatted sql

--changeset postgres:1

CREATE TABLE "user_roles" (
    "user_id" bigint UNIQUE,
    "role_id" bigint UNIQUE,
     PRIMARY KEY ("user_id", "role_id")
);
-- rollback drop table applicationinfo