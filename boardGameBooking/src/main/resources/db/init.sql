CREATE TABLE IF NOT EXISTS game
(
    id           UUID PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    min_players  INT,
    max_players  INT,
    playtime_min INT
);

INSERT INTO game (id, title, min_players, max_players, playtime_min)
VALUES ('11111111-1111-1111-1111-111111111111', 'Catan', 3, 4, 90),
       ('22222222-2222-2222-2222-222222222222', 'Wingspan', 1, 5, 80),
       ('33333333-3333-3333-3333-333333333333', 'Ticket to Ride', 2, 5, 60)
ON CONFLICT (id) DO NOTHING;
