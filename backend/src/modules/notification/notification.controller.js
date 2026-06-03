import * as notificationService from "./notification.service.js";

/**
 * GET /notifications
 * Get paginated notifications for the authenticated user
 */
export const getNotifications = async (req, res) => {
  try {
    const userId = req.user.id;
    const limit = parseInt(req.query.limit, 10) || 20;
    const offset = parseInt(req.query.offset, 10) || 0;

    const result = await notificationService.getNotifications(userId, limit, offset);

    res.json({
      success: true,
      data: {
        notifications: result.notifications,
        pagination: result.pagination,
        unreadCount: result.unreadCount,
      },
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

/**
 * GET /notifications/unread-count
 * Get total unread notification count
 */
export const getUnreadCount = async (req, res) => {
  try {
    const result = await notificationService.getUnreadCount(req.user.id);

    res.json({
      success: true,
      data: result,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

/**
 * PATCH /notifications/:id/read
 * Mark a single notification as read
 */
export const markRead = async (req, res) => {
  try {
    const { id } = req.params;
    const notification = await notificationService.markRead(id, req.user.id);

    res.json({
      success: true,
      data: notification,
    });
  } catch (error) {
    res.status(404).json({ success: false, error: error.message });
  }
};

/**
 * PATCH /notifications/read-all
 * Mark all notifications as read
 */
export const markAllRead = async (req, res) => {
  try {
    const result = await notificationService.markAllRead(req.user.id);

    res.json({
      success: true,
      data: result,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

/**
 * DELETE /notifications/:id
 * Soft delete a notification
 */
export const deleteNotification = async (req, res) => {
  try {
    const { id } = req.params;
    const result = await notificationService.removeNotification(id, req.user.id);

    res.json({
      success: true,
      data: result,
    });
  } catch (error) {
    res.status(404).json({ success: false, error: error.message });
  }
};

/**
 * GET /notifications/grouped
 * Get unread notifications grouped by conversation
 */
export const getGroupedNotifications = async (req, res) => {
  try {
    const result = await notificationService.getGroupedNotifications(req.user.id);

    res.json({
      success: true,
      data: result.groups,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

