CREATE TABLE "post" (
    "id" bigint generated always as identity primary key,
    "private" boolean,
    "text" varchar,
    "created_date" timestamp,
    "deleted" boolean,
    "id_user" bigint,
    "id_group" bigint
);