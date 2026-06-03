import { pool } from "../config/db.js";

/**
 * Find user by ID
 */
export const findUserById = async (id) => {
  const result = await pool.query(
    "SELECT id, username, email, avatar, status_message, is_online, last_seen, created_at FROM users WHERE id = $1",
    [id]
  );
  return result.rows[0];
};

/**
 * Find user by email
 */
export const findUserByEmail = async (email) => {
  const result = await pool.query(
    "SELECT * FROM users WHERE email = $1",
    [email]
  );
  return result.rows[0];
};

/**
 * Find user by username
 */
export const findUserByUsername = async (username) => {
  const result = await pool.query(
    "SELECT * FROM users WHERE username = $1",
    [username]
  );
  return result.rows[0];
};

/**
 * Create a new user
 */
export const createUser = async (username, email, password) => {
  const result = await pool.query(
    "INSERT INTO users (username, email, password) VALUES ($1, $2, $3) RETURNING *",
    [username, email, password]
  );
  return result.rows[0];
};

/**
 * Update user
 */
export const updateUser = async (id, updates) => {
  const { username, email, avatar, status_message, is_online, last_seen, fcm_token } = updates;

  const result = await pool.query(
    `UPDATE users 
     SET username = COALESCE($1, username),
         email = COALESCE($2, email),
         avatar = COALESCE($3, avatar),
         status_message = COALESCE($4, status_message),
         is_online = COALESCE($5, is_online),
         last_seen = COALESCE($6, last_seen),
         fcm_token = COALESCE($7, fcm_token)
     WHERE id = $8
     RETURNING id, username, email, avatar, status_message, is_online, last_seen, fcm_token, created_at`,
    [username, email, avatar, status_message, is_online, last_seen, fcm_token, id]
  );
  return result.rows[0];
};

/**
 * Update user online status
 */
export const updateOnlineStatus = async (id, isOnline) => {
  const result = await pool.query(
    "UPDATE users SET is_online = $1, last_seen = CURRENT_TIMESTAMP WHERE id = $2 RETURNING *",
    [isOnline, id]
  );
  return result.rows[0];
};

/**
 * Search users excluding a specific user with pagination
 */
export const searchUsers = async (excludeUserId, search, limit = 20, offset = 0) => {
  const result = await pool.query(
    `SELECT id, username, email, avatar, status_message, is_online, last_seen 
     FROM users 
     WHERE (username ILIKE $1 OR email ILIKE $1)
     AND id != $2
     ORDER BY username ASC
     LIMIT $3 OFFSET $4`,
    [`%${search}%`, excludeUserId, limit, offset]
  );
  return result.rows;
};

/**
 * Get all users
 */
export const getAllUsers = async () => {
  const result = await pool.query(
    "SELECT id, username, email, avatar, status_message, is_online, last_seen, created_at FROM users"
  );
  return result.rows;
};

/**
 * Delete user
 */
export const deleteUser = async (id) => {
  const result = await pool.query(
    "DELETE FROM users WHERE id = $1 RETURNING id",
    [id]
  );
  return result.rows[0];
};
