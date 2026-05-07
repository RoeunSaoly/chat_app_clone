import express from "express";
import authRoutes from "../modules/auth/auth.routes.js";
import chatRoutes from "./chat.routes.js";
import userRoutes from "../modules/user/user.routes.js";
import notificationRoutes from "../modules/notification/notification.routes.js";
import authMiddleware from "../middleware/auth.middleware.js";
import {
  getConversations,
  getConversation,
  createConversation,
  createGroupConversation,
  createPrivateConversation,
} from "../modules/conversation/conversation.controller.js";
import {
  getMessages,
  markMessagesSeen,
  reactToMessage,
  sendMessage,
  updateTypingStatus,
} from "../modules/message/message.controller.js";

const router = express.Router();

// API root
router.get("/", (req, res) => {
    res.json({
        message: "API working"
    });
});

// auth routes
router.use("/users", authRoutes);

// required user profile routes
router.use("/users", userRoutes);

// legacy user profile routes
router.use("/profile", userRoutes);

// required chat API routes
router.get("/conversations", authMiddleware, getConversations);
router.get("/conversations/:id", authMiddleware, getConversation);
router.post("/conversations", authMiddleware, createConversation);
router.post("/conversations/private", authMiddleware, createPrivateConversation);
router.post("/conversations/group", authMiddleware, createGroupConversation);
router.post("/messages", authMiddleware, sendMessage);
router.get("/messages/:conversationId", authMiddleware, getMessages);
router.post("/messages/seen", authMiddleware, markMessagesSeen);
router.post("/messages/react", authMiddleware, reactToMessage);
router.post("/typing", authMiddleware, updateTypingStatus);

// chat routes
router.use("/chat", chatRoutes);

// notification routes
router.use("/notifications", notificationRoutes);

export default router;
