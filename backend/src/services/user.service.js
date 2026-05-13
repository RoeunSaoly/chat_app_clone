import * as userRepository from "../repositories/user.repository.js";
import { AppError } from "../middleware/error.middleware.js";

export const getUserById = async (id) => {
  const user = await userRepository.findUserById(id);
  if (!user) throw new AppError("User not found", 404);
  return user;
};

export const searchUsers = async (excludeUserId, search, limit = 20, offset = 0) => {
  return userRepository.searchUsers(excludeUserId, search, limit, offset);
};

export const updateUser = async (id, data) => {
  const user = await userRepository.updateUser(id, data);
  if (!user) throw new AppError("User not found", 404);
  return user;
};

export const updateOnlineStatus = async (id, isOnline) => {
  return userRepository.updateOnlineStatus(id, isOnline);
};

export const deleteUser = async (id) => {
  return userRepository.deleteUser(id);
};
