import * as messageRepository from "../repositories/message.repository.js";
import * as notificationService from "../modules/notification/notification.service.js";
import { emitToConversation, emitToUser } from "../utils/socketEmitter.js";
import { canAccessConversation } from "./conversation.service.js";
import { AppError } from "../middleware/error.middleware.js";

/**
 * Send a message and create notifications for all conversation members
 */
export const sendMessage = async (senderId, conversationId, content, messageType = "text", replyTo = null) => {
  const canAccess = await canAccessConversation(conversationId, senderId);
  if (!canAccess) {
    throw new AppError("User is not a member of this conversation", 403);
  }

  // 1. Create the message
  const message = await messageRepository.createMessage({
    conversation_id: conversationId,
    sender_id: senderId,
    content,
    message_type: messageType,
    reply_to: replyTo,
  });

  // 2. Get sender username
  const senderUsername = await messageRepository.getSenderUsername(senderId);

  // 3. Get conversation members excluding sender
  const members = await messageRepository.getConversationMembers(conversationId, senderId);

  // 4. Mark as delivered when there are recipients. Read receipts are only written when seen.
  const memberIds = members.map((m) => m.user_id);
  if (memberIds.length > 0) {
    await messageRepository.updateMessageStatus(message.id, "delivered");
    message.status = "delivered";
  }

  // 5. Create notification for each member
  const notificationPromises = members.map(async (member) => {
    try {
      return await notificationService.createNotification({
        user_id: member.user_id,
        type: "message",
        title: senderUsername || "New Message",
        content: content ? content.substring(0, 50) : "",
        related_id: conversationId,
      });
    } catch (error) {
      console.error("notification error:", error.message);
      return null;
    }
  });

  await Promise.all(notificationPromises);

  const reactions = await messageRepository.getMessageReactions(message.id);
  const readBy = await messageRepository.getMessageReads(message.id);

  // 6. Emit real-time event to conversation room
  const messageWithSender = {
    ...message,
    sender_username: senderUsername,
    read_by: readBy,
    reactions,
  };

  emitToConversation(conversationId, "receive_message", messageWithSender);
  emitToConversation(conversationId, "new_message", messageWithSender);

  // 7. Emit delivered status to sender
  emitToUser(senderId, "message_delivered", {
    message_id: message.id,
    conversation_id: conversationId,
    status: message.status,
  });

  return {
    message: messageWithSender,
    notifiedUsers: memberIds,
  };
};

/**
 * Update typing indicator through REST or Socket.IO.
 */
export const updateTypingStatus = async (conversationId, userId, isTyping) => {
  const canAccess = await canAccessConversation(conversationId, userId);
  if (!canAccess) {
    throw new AppError("User is not a member of this conversation", 403);
  }

  const typing = await messageRepository.updateTypingStatus(conversationId, userId, isTyping);
  const typingUsers = await messageRepository.getTypingUsers(conversationId, userId);

  emitToConversation(conversationId, "typing", {
    conversation_id: conversationId,
    user_id: userId,
    is_typing: isTyping,
    typing_users: typingUsers,
  });

  return {
    ...typing,
    typing_users: typingUsers,
  };
};

/**
 * Add, update, or remove a reaction on a message.
 */
export const reactToMessage = async (messageId, userId, reaction) => {
  const message = await messageRepository.getMessageById(messageId);
  if (!message) {
    throw new AppError("Message not found", 404);
  }

  const canAccess = await canAccessConversation(message.conversation_id, userId);
  if (!canAccess) {
    throw new AppError("User is not a member of this conversation", 403);
  }

  const result = reaction
    ? await messageRepository.upsertReaction(messageId, userId, reaction)
    : await messageRepository.deleteReaction(messageId, userId);

  const reactions = await messageRepository.getMessageReactions(messageId);

  emitToConversation(message.conversation_id, "message_reaction", {
    message_id: messageId,
    user_id: userId,
    reaction: reaction || null,
    reactions,
  });

  return {
    reaction: result,
    reactions,
  };
};

/**
 * Handle send message via Socket.IO with full real-time flow
 */
export const handleSendMessageViaSocket = async (senderId, conversationId, content, messageType = "text", replyTo = null) => {
  return sendMessage(senderId, conversationId, content, messageType, replyTo);
};

/**
 * Handle typing indicator
 */
export const handleTyping = async (conversationId, userId, isTyping) => {
  return updateTypingStatus(conversationId, userId, isTyping);
};

/**
 * Handle message seen
 */
export const handleMessageSeen = async (messageId, userId) => {
  // Get message info for broadcasting
  const message = await messageRepository.getMessageById(messageId);
  if (!message) return null;

  const canAccess = await canAccessConversation(message.conversation_id, userId);
  if (!canAccess) {
    throw new AppError("User is not a member of this conversation", 403);
  }

  const readEntry = await messageRepository.markMessageAsSeen(messageId, userId);

  // Update message status to seen
  await messageRepository.updateMessageStatus(messageId, "seen");

  // Emit to conversation that this user has seen the message
  emitToConversation(message.conversation_id, "message_seen", {
    message_id: messageId,
    user_id: userId,
    conversation_id: message.conversation_id,
    seen_at: readEntry.seen_at,
  });

  return readEntry;
};

/**
 * Mark all messages in a conversation as seen
 */
export const markAllMessagesAsSeen = async (conversationId, userId) => {
  const canAccess = await canAccessConversation(conversationId, userId);
  if (!canAccess) {
    throw new AppError("User is not a member of this conversation", 403);
  }

  const messageIds = await messageRepository.markMessagesAsSeen(conversationId, userId);
  await messageRepository.updateMessagesStatus(messageIds, "seen");

  if (messageIds.length > 0) {
    emitToConversation(conversationId, "message_seen", {
      conversation_id: conversationId,
      user_id: userId,
      message_ids: messageIds,
    });
  }

  return messageIds;
};

/**
 * Get paginated messages for a conversation
 */
export const getMessages = async (conversationId, userId, limit = 50, cursor = null) => {
  const canAccess = await canAccessConversation(conversationId, userId);
  if (!canAccess) {
    throw new AppError("User is not a member of this conversation", 403);
  }

  const messages = await messageRepository.getMessagesByConversation(conversationId, userId, limit, cursor);

  // Attach read receipts to each message
  const messagesWithReads = await Promise.all(
    messages.map(async (msg) => {
      const [reads, reactions] = await Promise.all([
        messageRepository.getMessageReads(msg.id),
        messageRepository.getMessageReactions(msg.id),
      ]);
      return {
        ...msg,
        read_by: reads,
        reactions,
      };
    })
  );

  return messagesWithReads;
};

/**
 * Delete a message for the current user only.
 */
export const deleteMessageForMe = async (messageId, userId) => {
    return await messageRepository.deleteMessageForMe(messageId, userId);
};

/**
 * Delete a message for everyone in the conversation.
 */
export const deleteMessageForEveryone = async (messageId, userId) => {
    const message = await messageRepository.getMessageById(messageId);
    if (!message) throw new AppError("Message not found", 404);
    
    if (message.sender_id !== userId) {
        throw new AppError("You can only delete your own messages for everyone", 403);
    }

    const result = await messageRepository.deleteMessageForEveryone(messageId);
    
    // Notify all members via socket
    emitToConversation(message.conversation_id, "message_deleted", {
        message_id: messageId,
        conversation_id: message.conversation_id,
        deleted_for_everyone: true
    });

    return result;
};

/**
 * Edit a message
 */
export const editMessage = async (messageId, userId, content) => {
  const message = await messageRepository.getMessageById(messageId);
  if (!message) throw new AppError("Message not found", 404);

  if (message.sender_id !== userId) {
    throw new AppError("You can only edit your own messages", 403);
  }

  if (message.deleted_for_everyone) {
    throw new AppError("Cannot edit a deleted message", 400);
  }

  const updatedMessage = await messageRepository.editMessageContent(messageId, content);

  emitToConversation(message.conversation_id, "message_edited", {
    message_id: messageId,
    conversation_id: message.conversation_id,
    content: updatedMessage.content,
  });

  return updatedMessage;
};
