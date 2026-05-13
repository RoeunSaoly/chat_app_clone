import * as userService from "../services/user.service.js";
import { asyncHandler } from "../utils/asyncHandler.js";

// GET PROFILE
export const getProfile = asyncHandler(async (req, res) => {
  const user = await userService.getUserById(req.user.id);
  res.json({
    success: true,
    data: user,
  });
});

// SEARCH USERS
export const searchUsers = asyncHandler(async (req, res) => {
  const search = req.query.search || "";
  const limit = Math.min(parseInt(req.query.limit, 10) || 20, 50);
  const offset = parseInt(req.query.offset, 10) || 0;

  const users = await userService.searchUsers(req.user.id, search, limit, offset);
  
  res.json({
    success: true,
    data: users,
    pagination: {
      limit,
      offset,
      count: users.length,
    }
  });
});

// UPDATE PROFILE
export const updateProfile = asyncHandler(async (req, res) => {
  const updatedUser = await userService.updateUser(req.user.id, req.body);
  res.json({
    success: true,
    data: updatedUser,
  });
});

// DELETE USER
export const deleteUser = asyncHandler(async (req, res) => {
  await userService.deleteUser(req.user.id);
  res.json({ success: true, message: "User deleted" });
});
