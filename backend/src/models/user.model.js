import { pool } from "../config/db.js";

/**
 * User Model - Database operations for users table
 */

export const createUser = async (username, email, password) => {
    const result = await pool.query(
        "INSERT INTO users (username, email, password) VALUES ($1, $2, $3) RETURNING *",
        [username, email, password]
    );
    return result.rows[0];
};

export const findUserById = async (id) => {
    const result = await pool.query(
        "SELECT id, username, email, avatar, status_message, is_online, last_seen, created_at FROM users WHERE id = $1",
        [id]
    );
    return result.rows[0];
};

export const findUserByEmail = async (email) => {
    const result = await pool.query(
        "SELECT * FROM users WHERE email = $1",
        [email]
    );
    return result.rows[0];
};

export const findUserByUsername = async (username) => {
    const result = await pool.query(
        "SELECT * FROM users WHERE username = $1",
        [username]
    );
    return result.rows[0];
};

export const updateUser = async (id, updates) => {
    const { username, email, avatar, status_message, is_online, last_seen } = updates;
    
    const result = await pool.query(
        `UPDATE users 
         SET username = COALESCE($1, username),
             email = COALESCE($2, email),
             avatar = COALESCE($3, avatar),
             status_message = COALESCE($4, status_message),
             is_online = COALESCE($5, is_online),
             last_seen = COALESCE($6, last_seen)
         WHERE id = $7
         RETURNING id, username, email, avatar, status_message, is_online, last_seen, created_at`,
        [username, email, avatar, status_message, is_online, last_seen, id]
    );
    return result.rows[0];
};

export const updateOnlineStatus = async (id, isOnline) => {
    const result = await pool.query(
        "UPDATE users SET is_online = $1, last_seen = CURRENT_TIMESTAMP WHERE id = $2 RETURNING *",
        [isOnline, id]
    );
    return result.rows[0];
};

export const searchUsers = async (query, currentUserId) => {
    const result = await pool.query(
        `SELECT id, username, email, avatar, status_message, is_online, last_seen 
         FROM users 
         WHERE username LIKE $1 OR email LIKE $1
         LIMIT 20`,
        [`%${query}%`]
    );
    return result.rows;
};

export const getAllUsers = async () => {
    const result = await pool.query(
        "SELECT id, username, email, avatar, status_message, is_online, last_seen, created_at FROM users"
    );
    return result.rows;
};
