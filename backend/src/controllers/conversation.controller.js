import * as conversationService from "../services/conversation.service.js";
import { asyncHandler } from "../utils/asyncHandler.js";
import { AppError } from "../middleware/error.middleware.js";

/**
 * GET /chat/conversations
 * Get all conversations for the authenticated user
 */
export const getConversations = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const conversations = await conversationService.getUserConversations(userId);

  res.json({
    success: true,
    data: conversations,
  });
});

/**
 * GET /chat/conversations/:id
 * Get a single conversation by ID
 */
export const getConversation = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { id } = req.params;

  const conversation = await conversationService.getConversationDetail(id, userId);

  res.json({
    success: true,
    data: conversation,
  });
});

/**
 * POST /chat/conversations
 * Create a new conversation
 */
export const createConversation = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { type, name, avatar } = req.body;
  const memberIds = req.body.member_ids || req.body.memberIds || req.body.participantIds;

  if (!["private", "group"].includes(type)) {
    throw new AppError("type must be private or group", 400);
  }

  if (!memberIds || !Array.isArray(memberIds) || memberIds.length === 0) {
    throw new AppError("member_ids must be a non-empty array", 400);
  }

  if (type === "private" && memberIds.length !== 1) {
    throw new AppError("private conversations require exactly one other member", 400);
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
});

export const createPrivateConversation = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const otherUserId = req.body.userId || req.body.user_id || req.body.memberId || req.body.member_id;

  const conversation = await conversationService.createPrivateConversation(userId, otherUserId);

  res.status(conversation.existing ? 200 : 201).json({
    success: true,
    data: conversation,
  });
});

export const createGroupConversation = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { name, avatar } = req.body;
  const memberIds = req.body.member_ids || req.body.memberIds || [];

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
});
