import * as messageService from "./message.service.js";

/**
 * POST /chat/message
 * Send a new message and trigger notifications
 */
export const sendMessage = async (req, res) => {
  try {
    const senderId = req.user.id;
    const conversationId = req.body.conversationId || req.body.conversation_id;
    const content = req.body.content;
    const messageType = req.body.messageType || req.body.message_type || "text";

    if (!conversationId || !content?.trim()) {
      return res.status(400).json({
        success: false,
        error: "conversationId and content are required",
      });
    }

    const result = await messageService.sendMessage(
      senderId,
      conversationId,
      content.trim(),
      messageType
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
    const userId = req.user.id;
    const { conversationId } = req.params;
    const limit = Math.min(parseInt(req.query.limit, 10) || 50, 100);
    const offset = parseInt(req.query.offset, 10) || 0;

    const messages = await messageService.getMessages(conversationId, userId, limit, offset);

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
    const conversationId = req.body.conversationId || req.body.conversation_id;

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

/**
 * POST /typing
 * Update typing status for the authenticated user.
 */
export const updateTypingStatus = async (req, res) => {
  try {
    const userId = req.user.id;
    const conversationId = req.body.conversationId || req.body.conversation_id;
    const isTyping = Boolean(req.body.isTyping ?? req.body.is_typing);

    if (!conversationId) {
      return res.status(400).json({
        success: false,
        error: "conversationId is required",
      });
    }

    const typing = await messageService.updateTypingStatus(conversationId, userId, isTyping);

    res.json({
      success: true,
      data: typing,
    });
  } catch (error) {
    const status = error.message.includes("not a member") ? 403 : 500;
    res.status(status).json({ success: false, error: error.message });
  }
};

/**
 * POST /messages/react
 * Add, update, or remove a reaction for a message.
 */
export const reactToMessage = async (req, res) => {
  try {
    const userId = req.user.id;
    const messageId = req.body.messageId || req.body.message_id;
    const reaction = req.body.reaction;

    if (!messageId) {
      return res.status(400).json({
        success: false,
        error: "messageId is required",
      });
    }

    const result = await messageService.reactToMessage(messageId, userId, reaction);

    res.json({
      success: true,
      data: result,
    });
  } catch (error) {
    const status = error.message.includes("not found") ? 404 : error.message.includes("not a member") ? 403 : 500;
    res.status(status).json({ success: false, error: error.message });
  }
};

/**
 * DELETE /messages/:messageId
 * Delete a message (supports type=me or type=everyone)
 */
export const deleteMessage = async (req, res) => {
    const userId = req.user.id;
    const { messageId } = req.params;
    const { type } = req.query; // 'me' or 'everyone'

    if (type === 'everyone') {
        await messageService.deleteMessageForEveryone(messageId, userId);
    } else {
        await messageService.deleteMessageForMe(messageId, userId);
    }

    res.json({ success: true, message: "Message deleted" });
};
