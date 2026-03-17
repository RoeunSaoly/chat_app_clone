import express from "express";
import authMiddleware from "../middleware/auth.middleware.js";

const router = express.Router();

// TODO: Implement chat controller and move these routes there
// TODO: /rooms - Get all rooms/conversations for user
// TODO: /messages/:roomId - Get messages for a conversation
// TODO: /message - Send a new message
// TODO: /message/:id - Edit/Delete message

/**
 * GET /chat/rooms
 * Get all conversations for the authenticated user
 * Requires: authentication token
 */
router.get("/rooms", authMiddleware, (req, res) => {
    // TODO: Implement get conversations
    // Should query conversation_members table joined with conversations
    // Return list of rooms with last message preview
    res.status(501).json({ 
        error: "Not implemented", 
        message: "Get rooms endpoint not yet implemented" 
    });
});

/**
 * GET /chat/messages/:conversationId
 * Get all messages for a conversation
 * Requires: authentication token
 */
router.get("/messages/:conversationId", authMiddleware, (req, res) => {
    // TODO: Implement get messages
    // Should query messages table filtered by conversation_id
    // Should also mark messages as seen
    const { conversationId } = req.params;
    res.status(501).json({ 
        error: "Not implemented", 
        message: "Get messages endpoint not yet implemented",
        conversationId 
    });
});

/**
 * POST /chat/message
 * Send a new message
 * Requires: authentication token
 * Body: { conversationId, content, messageType }
 */
router.post("/message", authMiddleware, (req, res) => {
    // TODO: Implement send message
    // Should insert into messages table
    // Should emit socket event for real-time delivery
    res.status(501).json({ 
        error: "Not implemented", 
        message: "Send message endpoint not yet implemented" 
    });
});

export default router;

