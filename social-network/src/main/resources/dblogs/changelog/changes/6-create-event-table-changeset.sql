CREATE TABLE "event" (
     "id" bigint generated always as identity primary key,
     "id_location" bigint,
     "start_date" timestamp,
     "end_date" timestamp,
     "id_user" bigint,
     "id_group" bigint
);