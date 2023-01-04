CREATE TABLE "comment" (
   "id" bigint generated always as identity primary key,
   "text" varchar,
   "created_date" timestamp,
   "deleted" boolean,
   "id_user" bigint,
   "id_replied_to" bigint,
   "id_post" bigint
);