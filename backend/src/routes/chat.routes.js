import express from "express";
import authMiddleware from "../middleware/auth.middleware.js";
import {
  sendMessage,
  getMessages,
  markMessagesSeen,
  reactToMessage,
  updateTypingStatus,
} from "../modules/message/message.controller.js";
import {
  getConversations,
  getConversation,
  createConversation,
  createGroupConversation,
  createPrivateConversation,
} from "../modules/conversation/conversation.controller.js";

const router = express.Router();

/**
 * GET /chat/conversations
 * Get all conversations for the authenticated user
 */
router.get("/conversations", authMiddleware, getConversations);

/**
 * GET /chat/conversations/:id
 * Get a single conversation by ID
 */
router.get("/conversations/:id", authMiddleware, getConversation);

/**
 * POST /chat/conversations
 * Create a new conversation
 */
router.post("/conversations", authMiddleware, createConversation);
router.post("/conversations/private", authMiddleware, createPrivateConversation);
router.post("/conversations/group", authMiddleware, createGroupConversation);

/**
 * POST /chat/message
 * Send a new message and trigger notifications
 * Requires: authentication token
 * Body: { conversationId, content, messageType }
 */
router.post("/message", authMiddleware, sendMessage);
router.post("/messages", authMiddleware, sendMessage);

/**
 * GET /chat/messages/:conversationId
 * Get paginated messages for a conversation
 */
router.get("/messages/:conversationId", authMiddleware, getMessages);

/**
 * PATCH /chat/messages/seen
 * Mark all messages in a conversation as seen
 */
router.patch("/messages/seen", authMiddleware, markMessagesSeen);
router.post("/messages/seen", authMiddleware, markMessagesSeen);
router.post("/messages/react", authMiddleware, reactToMessage);
router.post("/typing", authMiddleware, updateTypingStatus);

export default router;

