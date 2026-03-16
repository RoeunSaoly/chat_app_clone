-- Migration: Add username column to users table
-- Run this if you already have a database created

ALTER TABLE users ADD COLUMN username VARCHAR(255) UNIQUE;

-- If name column exists and you want to copy data to username:
-- UPDATE users SET username = name WHERE username IS NULL;
