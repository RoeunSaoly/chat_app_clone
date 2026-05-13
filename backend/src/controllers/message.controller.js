import * as messageService from "../services/message.service.js";
import { asyncHandler } from "../utils/asyncHandler.js";

/**
 * POST /chat/message
 * Send a new message and trigger notifications
 */
export const sendMessage = asyncHandler(async (req, res) => {
  const senderId = req.user.id;
  const conversationId = req.body.conversationId || req.body.conversation_id;
  const content = req.body.content;
  const messageType = req.body.messageType || req.body.message_type || "text";
  const replyTo = req.body.replyTo || req.body.reply_to || null;

  const result = await messageService.sendMessage(
    senderId,
    conversationId,
    content.trim(),
    messageType,
    replyTo
  );

  res.status(201).json({
    success: true,
    data: result.message,
    notifiedUsers: result.notifiedUsers,
  });
});

/**
 * GET /chat/messages/:conversationId
 * Get paginated messages for a conversation
 */
export const getMessages = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { conversationId } = req.params;
  const limit = Math.min(parseInt(req.query.limit, 10) || 50, 100);
  const cursor = req.query.cursor ? parseInt(req.query.cursor, 10) : null;

  const messages = await messageService.getMessages(conversationId, userId, limit, cursor);

  res.json({
    success: true,
    data: messages,
    pagination: {
      limit,
      cursor: messages.length > 0 ? messages[messages.length - 1].id : null,
      count: messages.length,
    },
  });
});

/**
 * PATCH /chat/messages/seen
 * Mark all messages in a conversation as seen
 */
export const markMessagesSeen = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const conversationId = req.body.conversationId || req.body.conversation_id;

  const messageIds = await messageService.markAllMessagesAsSeen(conversationId, userId);

  res.json({
    success: true,
    data: {
      conversation_id: conversationId,
      marked_as_seen: messageIds,
    },
  });
});

/**
 * POST /typing
 * Update typing status for the authenticated user.
 */
export const updateTypingStatus = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const conversationId = req.body.conversationId || req.body.conversation_id;
  const isTyping = Boolean(req.body.isTyping ?? req.body.is_typing);

  const typing = await messageService.updateTypingStatus(conversationId, userId, isTyping);

  res.json({
    success: true,
    data: typing,
  });
});

/**
 * POST /messages/react
 * Add, update, or remove a reaction for a message.
 */
export const reactToMessage = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const messageId = req.body.messageId || req.body.message_id;
  const reaction = req.body.reaction;

  const result = await messageService.reactToMessage(messageId, userId, reaction);

  res.json({
    success: true,
    data: result,
  });
});

/**
 * DELETE /messages/:messageId
 * Delete a message (supports type=me or type=everyone)
 */
export const deleteMessage = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { messageId } = req.params;
  const { type } = req.query; // 'me' or 'everyone'

  if (type === 'everyone') {
    await messageService.deleteMessageForEveryone(messageId, userId);
  } else {
    await messageService.deleteMessageForMe(messageId, userId);
  }

  res.json({ success: true, message: "Message deleted" });
});

/**
 * PATCH /messages/:messageId
 * Edit a message
 */
export const editMessage = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { messageId } = req.params;
  const { content } = req.body;

  if (!content?.trim()) {
    return res.status(400).json({ success: false, error: "Content is required" });
  }

  const updatedMessage = await messageService.editMessage(messageId, userId, content.trim());

  res.json({ success: true, data: updatedMessage });
});
