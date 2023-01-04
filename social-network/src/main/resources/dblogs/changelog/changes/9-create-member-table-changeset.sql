CREATE TABLE "member" (
    "id_user" bigint,
    "id_group" bigint,
    UNIQUE ("id_user", "id_group"),
    PRIMARY KEY ("id_user", "id_group")
);