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

/**
 * Get conversation detail with members
 */
export const getConversationDetail = async (conversationId, userId) => {
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

/**
 * Check if user can access a conversation
 */
export const canAccessConversation = async (conversationId, userId) => {
  return conversationModel.isConversationMember(conversationId, userId);
};

