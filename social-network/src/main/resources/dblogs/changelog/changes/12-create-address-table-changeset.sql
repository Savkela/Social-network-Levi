CREATE TABLE "address" (
    "id" bigint generated always as identity primary key,
    "country" varchar,
    "city" varchar,
    "street" varchar,
    "number" integer
);