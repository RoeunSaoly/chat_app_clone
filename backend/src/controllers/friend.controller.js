import * as friendRepository from "../repositories/friend.repository.js";
import * as userRepository from "../repositories/user.repository.js";
import { asyncHandler } from "../utils/asyncHandler.js";
import { createNotification } from "../modules/notification/notification.service.js";
import { sendPushNotification } from "../services/fcm.service.js";

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

  // Get sender info
  const sender = await userRepository.findUserById(userId);

  // Send notification to friendId
  await createNotification({
    user_id: parseInt(friendId),
    type: "friend_request",
    title: "New Friend Request",
    content: `${sender.username} sent you a friend request.`,
    related_id: userId
  });

  // Send Push Notification
  await sendPushNotification(parseInt(friendId), {
    title: "New Friend Request",
    body: `${sender.username} sent you a friend request.`,
    data: {
      type: "friend_request",
      senderId: userId.toString()
    }
  });

  res.json({ success: true, message: "Friend request sent" });
});

export const acceptFriendRequest = asyncHandler(async (req, res) => {
  const userId = req.user.id;
  const { friendId } = req.params;

  if (!friendId) {
    return res.status(400).json({ success: false, error: "Friend ID required" });
  }

  await friendRepository.acceptFriendRequest(userId, parseInt(friendId));

  // Get current user info (the one who accepted)
  const user = await userRepository.findUserById(userId);

  // Send notification to the person who sent the request
  await createNotification({
    user_id: parseInt(friendId),
    type: "friend_accepted",
    title: "Friend Request Accepted",
    content: `${user.username} accepted your friend request.`,
    related_id: userId
  });

  // Send Push Notification
  await sendPushNotification(parseInt(friendId), {
    title: "Friend Request Accepted",
    body: `${user.username} accepted your friend request.`,
    data: {
      type: "friend_accepted",
      senderId: userId.toString()
    }
  });

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
