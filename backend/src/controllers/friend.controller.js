import * as friendRepository from "../repositories/friend.repository.js";
import { asyncHandler } from "../utils/asyncHandler.js";

export const sendFriendRequest = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { friendId } = req.params;

  if (!friendId) {
    return res.status(400).json({ success: false, error: "Friend ID required" });
  }

  if (userId === parseInt(friendId)) {
    return res.status(400).json({ success: false, error: "Cannot add yourself as friend" });
  }

  await friendRepository.sendFriendRequest(userId, parseInt(friendId));
  res.json({ success: true, message: "Friend request sent" });
});

export const acceptFriendRequest = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { friendId } = req.params;

  if (!friendId) {
    return res.status(400).json({ success: false, error: "Friend ID required" });
  }

  await friendRepository.acceptFriendRequest(userId, parseInt(friendId));
  res.json({ success: true, message: "Friend request accepted" });
});

export const rejectFriendRequest = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { friendId } = req.params;

  if (!friendId) {
    return res.status(400).json({ success: false, error: "Friend ID required" });
  }

  await friendRepository.rejectFriendRequest(userId, parseInt(friendId));
  res.json({ success: true, message: "Friend request rejected" });
});

export const unfriend = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { friendId } = req.params;

  if (!friendId) {
    return res.status(400).json({ success: false, error: "Friend ID required" });
  }

  const success = await friendRepository.unfriend(userId, parseInt(friendId));
  
  if (!success) {
    return res.status(404).json({ success: false, error: "Not friends" });
  }

  res.json({ success: true, message: "Unfriended successfully" });
});

export const getFriends = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const friends = await friendRepository.getFriends(userId);
  res.json({ success: true, data: friends });
});

export const getFriendRequests = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const requests = await friendRepository.getFriendRequests(userId);
  res.json({ success: true, data: requests });
});

export const getRecommendedFriends = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const limit = req.query.limit ? parseInt(req.query.limit) : 20;
  const recommended = await friendRepository.getRecommendedFriends(userId, limit);
  res.json({ success: true, data: recommended });
});
