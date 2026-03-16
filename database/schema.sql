CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(255) UNIQUE,
  email VARCHAR(255) UNIQUE,
  password TEXT
);

CREATE TABLE rooms (
  id SERIAL PRIMARY KEY,
  name TEXT
);

CREATE TABLE messages (
  id SERIAL PRIMARY KEY,
  room_id INT,
  user_id INT,
  message TEXT,
  created_at TIMESTAMP DEFAULT NOW()
);