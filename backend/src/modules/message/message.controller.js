import * as messageService from "./message.service.js";

/**
 * POST /chat/message
 * Send a new message and trigger notifications
 */
export const sendMessage = async (req, res) => {
  try {
    const senderId = req.user.id;
    const { conversationId, content, messageType } = req.body;

    if (!conversationId || !content) {
      return res.status(400).json({
        success: false,
        error: "conversationId and content are required",
      });
    }

    const result = await messageService.sendMessage(
      senderId,
      conversationId,
      content,
      messageType || "text"
    );

    res.status(201).json({
      success: true,
      data: result.message,
      notifiedUsers: result.notifiedUsers,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

/**
 * GET /chat/messages/:conversationId
 * Get paginated messages for a conversation
 */
export const getMessages = async (req, res) => {
  try {
    const { conversationId } = req.params;
    const limit = parseInt(req.query.limit, 10) || 50;
    const offset = parseInt(req.query.offset, 10) || 0;

    const messages = await messageService.getMessages(conversationId, limit, offset);

    res.json({
      success: true,
      data: messages,
      pagination: {
        limit,
        offset,
        count: messages.length,
      },
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

/**
 * PATCH /chat/messages/seen
 * Mark all messages in a conversation as seen
 */
export const markMessagesSeen = async (req, res) => {
  try {
    const userId = req.user.id;
    const { conversationId } = req.body;

    if (!conversationId) {
      return res.status(400).json({
        success: false,
        error: "conversationId is required",
      });
    }

    const messageIds = await messageService.markAllMessagesAsSeen(conversationId, userId);

    res.json({
      success: true,
      data: {
        conversation_id: conversationId,
        marked_as_seen: messageIds,
      },
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

