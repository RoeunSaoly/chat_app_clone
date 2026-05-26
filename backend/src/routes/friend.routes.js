import express from "express";
import {
  sendFriendRequest,
  acceptFriendRequest,
  rejectFriendRequest,
  unfriend,
  getFriends,
  getFriendRequests,
  getRecommendedFriends,
} from "../controllers/friend.controller.js";
import { verifyToken } from "../middleware/auth.middleware.js";

const router = express.Router();

// Apply auth middleware to all routes
router.use(verifyToken);

// Get endpoints
router.get("/", getFriends);
router.get("/requests", getFriendRequests);
router.get("/recommended", getRecommendedFriends);

// Post endpoints
router.post("/request/:friendId", sendFriendRequest);
router.post("/accept/:friendId", acceptFriendRequest);
router.post("/reject/:friendId", rejectFriendRequest);

// Delete endpoint
router.delete("/:friendId", unfriend);

export default router;
