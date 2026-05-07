import * as userModel from "./user.model.js";

export const getUserById = (id) => {
  return userModel.findUserById(id);
};

export const getAllUsers = (excludeUserId, search) => {
  return userModel.getAllUsers(excludeUserId, search);
};

export const updateUser = (id, data) => {
  return userModel.updateUser(id, data);
};

export const updateOnlineStatus = (id, isOnline) => {
  return userModel.updateOnlineStatus(id, isOnline);
};

export const deleteUser = (id) => {
  return userModel.deleteUser(id);
};
