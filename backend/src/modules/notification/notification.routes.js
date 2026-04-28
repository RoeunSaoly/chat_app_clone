import express from "express";
import {
  getNotifications,
  getUnreadCount,
  markRead,
  markAllRead,
  deleteNotification,
  getGroupedNotifications,
} from "./notification.controller.js";
import { verifyToken } from "../../middleware/auth.middleware.js";

const router = express.Router();

// All notification routes require authentication
router.get("/", verifyToken, getNotifications);
router.get("/unread-count", verifyToken, getUnreadCount);
router.get("/grouped", verifyToken, getGroupedNotifications);
router.patch("/:id/read", verifyToken, markRead);
router.patch("/read-all", verifyToken, markAllRead);
router.delete("/:id", verifyToken, deleteNotification);

export default router;

