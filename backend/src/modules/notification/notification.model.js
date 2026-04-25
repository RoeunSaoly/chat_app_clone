import { pool } from "../../config/db.js";

/**
 * Create a new notification
 */
export const createNotification = async (data) => {
  const { user_id, type, title, content, related_id } = data;

  const result = await pool.query(
    `INSERT INTO notifications (user_id, type, title, content, related_id)
     VALUES ($1, $2, $3, $4, $5)
     RETURNING id, user_id, type, title, content, is_read, related_id, created_at`,
    [user_id, type, title, content, related_id]
  );

  return result.rows[0];
};

/**
 * Get paginated notifications for a user
 */
export const getUserNotifications = async (userId, limit = 20, offset = 0) => {
  const result = await pool.query(
    `SELECT id, user_id, type, title, content, is_read, related_id, created_at
     FROM notifications
     WHERE user_id = $1 AND is_deleted = FALSE
     ORDER BY created_at DESC
     LIMIT $2 OFFSET $3`,
    [userId, limit, offset]
  );

  return result.rows;
};

/**
 * Get total notification count for a user
 */
export const getUserNotificationsCount = async (userId) => {
  const result = await pool.query(
    `SELECT COUNT(*)::int as total
     FROM notifications
     WHERE user_id = $1 AND is_deleted = FALSE`,
    [userId]
  );

  return result.rows[0].total;
};

/**
 * Mark a single notification as read
 */
export const markAsRead = async (notificationId, userId) => {
  const result = await pool.query(
    `UPDATE notifications
     SET is_read = TRUE
     WHERE id = $1 AND user_id = $2 AND is_deleted = FALSE
     RETURNING id, user_id, type, title, content, is_read, related_id, created_at`,
    [notificationId, userId]
  );

  return result.rows[0];
};

/**
 * Mark all notifications as read for a user
 */
export const markAllAsRead = async (userId) => {
  const result = await pool.query(
    `UPDATE notifications
     SET is_read = TRUE
     WHERE user_id = $1 AND is_read = FALSE AND is_deleted = FALSE
     RETURNING id`,
    [userId]
  );

  return result.rowCount;
};

/**
 * Get unread notification count for a user
 */
export const getUnreadCount = async (userId) => {
  const result = await pool.query(
    `SELECT COUNT(*)::int as count
     FROM notifications
     WHERE user_id = $1 AND is_read = FALSE AND is_deleted = FALSE`,
    [userId]
  );

  return result.rows[0].count;
};

/**
 * Soft delete a notification
 */
export const deleteNotification = async (notificationId, userId) => {
  const result = await pool.query(
    `UPDATE notifications
     SET is_deleted = TRUE
     WHERE id = $1 AND user_id = $2
     RETURNING id`,
    [notificationId, userId]
  );

  return result.rowCount > 0;
};

/**
 * Get notifications grouped by related conversation
 */
export const getNotificationsGrouped = async (userId) => {
  const result = await pool.query(
    `SELECT 
       related_id,
       COUNT(*)::int as unread_count,
       MAX(created_at) as latest_at
     FROM notifications
     WHERE user_id = $1 AND is_read = FALSE AND is_deleted = FALSE AND related_id IS NOT NULL
     GROUP BY related_id
     ORDER BY latest_at DESC`,
    [userId]
  );

  return result.rows;
};

