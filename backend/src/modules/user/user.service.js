import * as userModel from "./user.model.js";

export const getUserById = (id) => {
  return userModel.findUserById(id);
};

export const updateUser = (id, data) => {
  return userModel.updateUser(id, data);
};

export const deleteUser = (id) => {
  return userModel.deleteUser(id);
};