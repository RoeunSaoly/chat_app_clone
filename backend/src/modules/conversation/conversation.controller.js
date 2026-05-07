import * as conversationService from "./conversation.service.js";

/**
 * GET /chat/conversations
 * Get all conversations for the authenticated user
 */
export const getConversations = async (req, res) => {
  try {
    const userId = req.user.id;
    const conversations = await conversationService.getUserConversations(userId);

    res.json({
      success: true,
      data: conversations,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

/**
 * GET /chat/conversations/:id
 * Get a single conversation by ID
 */
export const getConversation = async (req, res) => {
  try {
    const userId = req.user.id;
    const { id } = req.params;

    const conversation = await conversationService.getConversationDetail(id, userId);

    if (!conversation) {
      return res.status(404).json({
        success: false,
        error: "Conversation not found",
      });
    }

    res.json({
      success: true,
      data: conversation,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

/**
 * POST /chat/conversations
 * Create a new conversation
 */
export const createConversation = async (req, res) => {
  try {
    const userId = req.user.id;
    const { type, name, avatar } = req.body;
    const memberIds = req.body.member_ids || req.body.memberIds || req.body.participantIds;

    if (!["private", "group"].includes(type)) {
      return res.status(400).json({
        success: false,
        error: "type must be private or group",
      });
    }

    if (!memberIds || !Array.isArray(memberIds) || memberIds.length === 0) {
      return res.status(400).json({
        success: false,
        error: "member_ids must be a non-empty array",
      });
    }

    if (type === "private" && memberIds.length !== 1) {
      return res.status(400).json({
        success: false,
        error: "private conversations require exactly one other member",
      });
    }

    const conversation = await conversationService.createConversation({
      type,
      name,
      avatar,
      created_by: userId,
      member_ids: memberIds,
    });

    res.status(201).json({
      success: true,
      data: conversation,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const createPrivateConversation = async (req, res) => {
  try {
    const userId = req.user.id;
    const otherUserId = req.body.userId || req.body.user_id || req.body.memberId || req.body.member_id;

    if (!otherUserId || Number(otherUserId) === Number(userId)) {
      return res.status(400).json({
        success: false,
        error: "A different userId is required",
      });
    }

    const conversation = await conversationService.createPrivateConversation(userId, otherUserId);

    res.status(conversation.existing ? 200 : 201).json({
      success: true,
      data: conversation,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

export const createGroupConversation = async (req, res) => {
  try {
    const userId = req.user.id;
    const { name, avatar } = req.body;
    const memberIds = req.body.member_ids || req.body.memberIds || [];

    if (!name?.trim()) {
      return res.status(400).json({ success: false, error: "name is required" });
    }

    if (!Array.isArray(memberIds) || memberIds.length === 0) {
      return res.status(400).json({
        success: false,
        error: "member_ids must be a non-empty array",
      });
    }

    const conversation = await conversationService.createGroupConversation(
      userId,
      name.trim(),
      memberIds,
      avatar
    );

    res.status(201).json({
      success: true,
      data: conversation,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

