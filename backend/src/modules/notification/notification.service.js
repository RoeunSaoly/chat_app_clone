import * as notificationModel from "./notification.model.js";
import { emitToUser } from "../../utils/socketEmitter.js";

/**
 * Create a notification and emit real-time event
 */
export const createNotification = async (data) => {
  const notification = await notificationModel.createNotification(data);

  // Emit real-time event to the recipient
  emitToUser(data.user_id, "new_notification", notification);

  return notification;
};

/**
 * Get paginated notifications with metadata
 */
export const getNotifications = async (userId, limit, offset) => {
  const [notifications, total, unreadCount] = await Promise.all([
    notificationModel.getUserNotifications(userId, limit, offset),
    notificationModel.getUserNotificationsCount(userId),
    notificationModel.getUnreadCount(userId),
  ]);

  return {
    notifications,
    pagination: {
      total,
      limit: parseInt(limit, 10),
      offset: parseInt(offset, 10),
      hasMore: parseInt(offset, 10) + notifications.length < total,
    },
    unreadCount,
  };
};

/**
 * Mark a notification as read
 */
export const markRead = async (notificationId, userId) => {
  const notification = await notificationModel.markAsRead(notificationId, userId);

  if (!notification) {
    throw new Error("Notification not found");
  }

  return notification;
};

/**
 * Mark all notifications as read for a user
 */
export const markAllRead = async (userId) => {
  const updatedCount = await notificationModel.markAllAsRead(userId);
  return { updatedCount };
};

/**
 * Get unread notification count
 */
export const getUnreadCount = async (userId) => {
  const count = await notificationModel.getUnreadCount(userId);
  return { count };
};

/**
 * Soft delete a notification
 */
export const removeNotification = async (notificationId, userId) => {
  const deleted = await notificationModel.deleteNotification(notificationId, userId);

  if (!deleted) {
    throw new Error("Notification not found or already deleted");
  }

  return { deleted: true };
};

/**
 * Get grouped unread notifications by conversation
 */
export const getGroupedNotifications = async (userId) => {
  const groups = await notificationModel.getNotificationsGrouped(userId);
  return { groups };
};

