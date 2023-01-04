CREATE TABLE "hidden_from" (
    "id_post" bigint,
    "id_user" bigint,
    PRIMARY KEY ("id_post", "id_user")
);