import * as conversationRepository from "../repositories/conversation.repository.js";
import { AppError } from "../middleware/error.middleware.js";

/**
 * Get all conversations for a user with member details
 */
export const getUserConversations = async (userId) => {
  const conversations = await conversationRepository.getConversationsByUser(userId);

  // Fetch members for each conversation
  const conversationsWithMembers = await Promise.all(
    conversations.map(async (conv) => {
      const members = await conversationRepository.getConversationMembers(conv.id);
      // For private chats, get the other user details
      const otherMember =
        conv.type === "private"
          ? members.find((m) => m.user_id !== parseInt(userId))
          : null;

      return {
        ...conv,
        members,
        other_user: otherMember || null,
      };
    })
  );

  return conversationsWithMembers;
};

export const getUserConversationIds = async (userId) => {
  return conversationRepository.getConversationIdsByUser(userId);
};

/**
 * Get conversation detail with members
 */
export const getConversationDetail = async (conversationId, userId) => {
  const canAccess = await conversationRepository.isConversationMember(conversationId, userId);
  if (!canAccess) throw new AppError("Access denied or conversation not found", 403);

  const conversation = await conversationRepository.getConversationById(conversationId);
  if (!conversation) throw new AppError("Conversation not found", 404);

  const members = await conversationRepository.getConversationMembers(conversationId);

  return {
    ...conversation,
    members,
  };
};

/**
 * Create a new conversation (private or group)
 */
export const createConversation = async (data) => {
  const { type, name, avatar, created_by, member_ids } = data;

  // Ensure creator is in member list
  const allMembers = [...new Set([...member_ids, created_by])];

  if (type === "private" && allMembers.length === 2) {
    const existing = await conversationRepository.findPrivateConversation(allMembers[0], allMembers[1]);
    if (existing) {
      const members = await conversationRepository.getConversationMembers(existing.id);
      return {
        ...existing,
        members,
        existing: true,
      };
    }
  }

  const conversation = await conversationRepository.createConversation({
    type,
    name,
    avatar,
    created_by,
  });

  await conversationRepository.addConversationMembers(conversation.id, allMembers);

  const members = await conversationRepository.getConversationMembers(conversation.id);

  return {
    ...conversation,
    members,
  };
};

export const createPrivateConversation = async (createdBy, otherUserId) => {
  return createConversation({
    type: "private",
    created_by: createdBy,
    member_ids: [Number(otherUserId)],
  });
};

export const createGroupConversation = async (createdBy, name, memberIds, avatar = null) => {
  return createConversation({
    type: "group",
    name,
    avatar,
    created_by: createdBy,
    member_ids: memberIds.map(Number),
  });
};

/**
 * Check if user can access a conversation
 */
export const canAccessConversation = async (conversationId, userId) => {
  return conversationRepository.isConversationMember(conversationId, userId);
};
