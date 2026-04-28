import * as conversationModel from "./conversation.model.js";

/**
 * Get all conversations for a user with member details
 */
export const getUserConversations = async (userId) => {
  const conversations = await conversationModel.getConversationsByUser(userId);

  // Fetch members for each conversation
  const conversationsWithMembers = await Promise.all(
    conversations.map(async (conv) => {
      const members = await conversationModel.getConversationMembers(conv.id);
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
  return conversationModel.getConversationIdsByUser(userId);
};

/**
 * Get conversation detail with members
 */
export const getConversationDetail = async (conversationId, userId) => {
  const canAccess = await conversationModel.isConversationMember(conversationId, userId);
  if (!canAccess) return null;

  const conversation = await conversationModel.getConversationById(conversationId);
  if (!conversation) return null;

  const members = await conversationModel.getConversationMembers(conversationId);

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
    const existing = await conversationModel.findPrivateConversation(allMembers[0], allMembers[1]);
    if (existing) {
      const members = await conversationModel.getConversationMembers(existing.id);
      return {
        ...existing,
        members,
        existing: true,
      };
    }
  }

  const conversation = await conversationModel.createConversation({
    type,
    name,
    avatar,
    created_by,
  });

  await conversationModel.addConversationMembers(conversation.id, allMembers);

  const members = await conversationModel.getConversationMembers(conversation.id);

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
  return conversationModel.isConversationMember(conversationId, userId);
};

