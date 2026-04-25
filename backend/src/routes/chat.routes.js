import express from "express";
import authMiddleware from "../middleware/auth.middleware.js";
import {
  sendMessage,
  getMessages,
  markMessagesSeen,
} from "../modules/message/message.controller.js";
import {
  getConversations,
  getConversation,
  createConversation,
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

/**
 * POST /chat/message
 * Send a new message and trigger notifications
 * Requires: authentication token
 * Body: { conversationId, content, messageType }
 */
router.post("/message", authMiddleware, sendMessage);

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

export default router;

