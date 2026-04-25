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
    const { type, name, avatar, member_ids } = req.body;

    if (!type || !member_ids || !Array.isArray(member_ids)) {
      return res.status(400).json({
        success: false,
        error: "type and member_ids are required",
      });
    }

    const conversation = await conversationService.createConversation({
      type,
      name,
      avatar,
      created_by: userId,
      member_ids,
    });

    res.status(201).json({
      success: true,
      data: conversation,
    });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
};

