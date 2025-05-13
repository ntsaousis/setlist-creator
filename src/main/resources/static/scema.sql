-- schema.sql
CREATE TABLE artist (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE setlist (
    id SERIAL PRIMARY KEY,
    artist_id INT REFERENCES artist(id),
    venue VARCHAR(100),
    event_date DATE
);
