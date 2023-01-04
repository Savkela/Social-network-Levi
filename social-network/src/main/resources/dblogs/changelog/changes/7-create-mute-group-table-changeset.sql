CREATE TABLE "mute_group" (
    "is_permanent" boolean,
    "end_of_mute" timestamp,
    "id_user" bigint,
    "id_group" bigint,
    PRIMARY KEY ("id_user", "id_group")
);