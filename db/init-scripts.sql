-- create extension hstore;
-- create schema users;
-- create table if not exists users."User" ("user_id" BIGSERIAL NOT NULL PRIMARY KEY,"name" VARCHAR NOT NULL,"password" VARCHAR NOT NULL);

-- Create the users table
CREATE TABLE IF NOT EXISTS users (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    password VARCHAR(255) NOT NULL
);

-- Insert an example user
INSERT INTO users (name, password)
VALUES ('john_doe', 'password123');
