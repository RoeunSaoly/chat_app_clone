import * as messageModel from "./message.model.js";
import * as notificationService from "../notification/notification.service.js";
import { emitToConversation, emitToUser } from "../../utils/socketEmitter.js";
import { canAccessConversation } from "../conversation/conversation.service.js";

/**
 * Send a message and create notifications for all conversation members
 */
export const sendMessage = async (senderId, conversationId, content, messageType = "text") => {
  const canAccess = await canAccessConversation(conversationId, senderId);
  if (!canAccess) {
    throw new Error("User is not a member of this conversation");
  }

  // 1. Create the message
  const message = await messageModel.createMessage({
    conversation_id: conversationId,
    sender_id: senderId,
    content,
    message_type: messageType,
  });

  // 2. Get sender username
  const senderUsername = await messageModel.getSenderUsername(senderId);

  // 3. Get conversation members excluding sender
  const members = await messageModel.getConversationMembers(conversationId, senderId);

  // 4. Mark as delivered when there are recipients. Read receipts are only written when seen.
  const memberIds = members.map((m) => m.user_id);
  if (memberIds.length > 0) {
    await messageModel.updateMessageStatus(message.id, "delivered");
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

  const reactions = await messageModel.getMessageReactions(message.id);
  const readBy = await messageModel.getMessageReads(message.id);

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
    throw new Error("User is not a member of this conversation");
  }

  const typing = await messageModel.updateTypingStatus(conversationId, userId, isTyping);
  const typingUsers = await messageModel.getTypingUsers(conversationId, userId);

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
  const message = await messageModel.getMessageById(messageId);
  if (!message) {
    throw new Error("Message not found");
  }

  const canAccess = await canAccessConversation(message.conversation_id, userId);
  if (!canAccess) {
    throw new Error("User is not a member of this conversation");
  }

  const result = reaction
    ? await messageModel.upsertReaction(messageId, userId, reaction)
    : await messageModel.deleteReaction(messageId, userId);

  const reactions = await messageModel.getMessageReactions(messageId);

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
export const handleSendMessageViaSocket = async (senderId, conversationId, content, messageType = "text") => {
  return sendMessage(senderId, conversationId, content, messageType);
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
  const message = await messageModel.getMessageById(messageId);
  if (!message) return null;

  const canAccess = await canAccessConversation(message.conversation_id, userId);
  if (!canAccess) {
    throw new Error("User is not a member of this conversation");
  }

  const readEntry = await messageModel.markMessageAsSeen(messageId, userId);

  // Update message status to seen
  await messageModel.updateMessageStatus(messageId, "seen");

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
    throw new Error("User is not a member of this conversation");
  }

  const messageIds = await messageModel.markMessagesAsSeen(conversationId, userId);
  await messageModel.updateMessagesStatus(messageIds, "seen");

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
export const getMessages = async (conversationId, userId, limit = 50, offset = 0) => {
  const canAccess = await canAccessConversation(conversationId, userId);
  if (!canAccess) {
    throw new Error("User is not a member of this conversation");
  }

  const messages = await messageModel.getMessagesByConversation(conversationId, limit, offset);

  // Attach read receipts to each message
  const messagesWithReads = await Promise.all(
    messages.map(async (msg) => {
      const [reads, reactions] = await Promise.all([
        messageModel.getMessageReads(msg.id),
        messageModel.getMessageReactions(msg.id),
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
    return await messageModel.deleteMessageForMe(messageId, userId);
};

/**
 * Delete a message for everyone in the conversation.
 */
export const deleteMessageForEveryone = async (messageId, userId) => {
    const message = await messageModel.getMessageById(messageId);
    if (!message) throw new Error("Message not found");
    
    if (message.sender_id !== userId) {
        throw new Error("You can only delete your own messages for everyone");
    }

    const result = await messageModel.deleteMessageForEveryone(messageId);
    
    // Notify all members via socket
    emitToConversation(message.conversation_id, "message_deleted", {
        message_id: messageId,
        conversation_id: message.conversation_id,
        deleted_for_everyone: true
    });

    return result;
};
