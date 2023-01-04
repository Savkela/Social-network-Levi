CREATE TABLE "member_event" (
    "id_user" bigint,
    "id_event" bigint,
    "id_group" bigint,
    UNIQUE("id_user", "id_event", "id_group"),
    PRIMARY KEY ("id_user", "id_event", "id_group")
);