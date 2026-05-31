import { pool } from "../config/db.js";

export const sendFriendRequest = async (userId, friendId) => {
  const result = await pool.query(
    `INSERT INTO friends (user_id, friend_id, status, requested_by)
     VALUES ($1, $2, 'pending', $1)
     ON CONFLICT (user_id, friend_id) DO UPDATE SET status = 'pending', updated_at = CURRENT_TIMESTAMP
     RETURNING *`,
    [userId, friendId]
  );
  return result.rows[0];
};

export const acceptFriendRequest = async (userId, friendId) => {
  const result = await pool.query(
    `UPDATE friends 
     SET status = 'accepted', updated_at = CURRENT_TIMESTAMP
     WHERE (user_id = $1 AND friend_id = $2 AND status = 'pending')
     OR (user_id = $2 AND friend_id = $1 AND status = 'pending')
     RETURNING *`,
    [userId, friendId]
  );
  return result.rows[0];
};

export const rejectFriendRequest = async (userId, friendId) => {
  const result = await pool.query(
    `DELETE FROM friends 
     WHERE (user_id = $1 AND friend_id = $2 AND status = 'pending')
     OR (user_id = $2 AND friend_id = $1 AND status = 'pending')
     RETURNING *`,
    [userId, friendId]
  );
  return result.rows[0];
};

export const unfriend = async (userId, friendId) => {
  const result = await pool.query(
    `DELETE FROM friends 
     WHERE (user_id = $1 AND friend_id = $2) 
     OR (user_id = $2 AND friend_id = $1)`,
    [userId, friendId]
  );
  return result.rowCount > 0;
};

export const getFriends = async (userId) => {
  const result = await pool.query(
    `SELECT u.id, u.username, u.email, u.avatar, u.is_online, u.last_seen
     FROM users u
     INNER JOIN friends f ON (
       (f.user_id = $1 AND f.friend_id = u.id) OR 
       (f.friend_id = $1 AND f.user_id = u.id)
     )
     WHERE f.status = 'accepted'
     ORDER BY u.username ASC`,
    [userId]
  );
  return result.rows;
};

export const getFriendRequests = async (userId) => {
  const result = await pool.query(
    `SELECT u.id, u.username, u.email, u.avatar, u.is_online, u.last_seen
     FROM users u
     INNER JOIN friends f ON f.user_id = u.id
     WHERE f.friend_id = $1 AND f.status = 'pending'
     ORDER BY f.created_at DESC`,
    [userId]
  );
  return result.rows;
};

export const getRecommendedFriends = async (userId, limit = 20) => {
  const result = await pool.query(
    `SELECT u.id, u.username, u.email, u.avatar, u.is_online, u.last_seen
     FROM users u
     WHERE u.id != $1
     AND u.id NOT IN (
       SELECT friend_id FROM friends WHERE user_id = $1
       UNION ALL
       SELECT user_id FROM friends WHERE friend_id = $1
     )
     ORDER BY u.created_at DESC
     LIMIT $2`,
    [userId, limit]
  );
  return result.rows;
};

export const isFriend = async (userId, friendId) => {
  const result = await pool.query(
    `SELECT 1 FROM friends 
     WHERE (user_id = $1 AND friend_id = $2 OR user_id = $2 AND friend_id = $1)
     AND status = 'accepted'`,
    [userId, friendId]
  );
  return result.rows.length > 0;
};

export const hasPendingRequest = async (userId, friendId) => {
  const result = await pool.query(
    `SELECT 1 FROM friends 
     WHERE (user_id = $1 AND friend_id = $2 OR user_id = $2 AND friend_id = $1)
     AND status = 'pending'`,
    [userId, friendId]
  );
  return result.rows.length > 0;
};
