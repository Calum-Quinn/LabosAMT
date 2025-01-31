CREATE TABLE users
(
    id       serial            NOT NULL PRIMARY KEY,
    username character varying NOT NULL,
    password character varying NOT NULL,
    role     character varying NOT NULL
);

INSERT INTO users (id, username, password, role)
VALUES (DEFAULT, 'bob', '$2a$10$I6/87HAuZmhoD13MhCzq4e64tSabiqFcrHad.U1Oy1NiQ78w/8CuS', 'user'),
       (DEFAULT, 'ana', '$2a$10$I6/87HAuZmhoD13MhCzq4e64tSabiqFcrHad.U1Oy1NiQ78w/8CuS', 'user');


