import { pool } from "../../config/db.js";

// GET USER
export const findUserById = async (id) => {
  const result = await pool.query(
    `SELECT id, username, email, avatar, status_message, is_online, last_seen
     FROM users WHERE id = $1`,
    [id]
  );
  return result.rows[0];
};

// GET ALL USERS
export const getAllUsers = async (excludeUserId, search = "") => {
  const keyword = `%${search.trim()}%`;
  const result = await pool.query(
    `SELECT id, username, email, avatar, status_message, is_online, last_seen
     FROM users
     WHERE id != $1
       AND ($2 = '%%' OR username ILIKE $2 OR email ILIKE $2)
     ORDER BY username ASC`,
    [excludeUserId, keyword]
  );
  return result.rows;
};

// UPDATE USER
export const updateUser = async (id, data) => {
  const { username, avatar, status_message } = data;

  const result = await pool.query(
    `UPDATE users
     SET username = COALESCE($1, username),
         avatar = COALESCE($2, avatar),
         status_message = COALESCE($3, status_message)
     WHERE id = $4
     RETURNING id, username, email, avatar, status_message, is_online, last_seen`,
    [username, avatar, status_message, id]
  );

  return result.rows[0];
};

// UPDATE ONLINE STATUS
export const updateOnlineStatus = async (id, isOnline) => {
  const result = await pool.query(
    `UPDATE users
     SET is_online = $2,
         last_seen = CASE WHEN $2 = FALSE THEN CURRENT_TIMESTAMP ELSE last_seen END
     WHERE id = $1
     RETURNING id, username, is_online, last_seen`,
    [id, isOnline]
  );

  return result.rows[0];
};

// DELETE USER
export const deleteUser = async (id) => {
  await pool.query(`DELETE FROM users WHERE id = $1`, [id]);
};
