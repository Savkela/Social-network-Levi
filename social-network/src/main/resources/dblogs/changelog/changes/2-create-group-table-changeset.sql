CREATE TABLE "group" (
     "id" bigint generated always as identity primary key,
     "name" varchar,
     "private" boolean,
     "id_admin" bigint
);