import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import * as userModel from "../models/user.model.js";

/**
 * User Service - Business logic for user operations
 */

export const registerUser = async (username, email, password) => {
    // Check if username already exists
    const existingUsername = await userModel.findUserByUsername(username);
    if (existingUsername) {
        throw new Error("Username already taken");
    }

    // Check if email already exists
    const existingEmail = await userModel.findUserByEmail(email);
    if (existingEmail) {
        throw new Error("Email already registered");
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    // Create user
    const user = await userModel.createUser(username, email, hashedPassword);
    
    // Remove password from returned user
    const { password: _, ...userWithoutPassword } = user;
    return userWithoutPassword;
};

export const loginUser = async (email, password) => {
    const user = await userModel.findUserByEmail(email);
    if (!user) {
        throw new Error("User not found");
    }

    const valid = await bcrypt.compare(password, user.password);
    if (!valid) {
        throw new Error("Invalid password");
    }

    const token = jwt.sign(
        { id: user.id, email: user.email },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRE || "1d" }
    );

    // Remove password from user object
    const { password: _, ...userWithoutPassword } = user;
    
    return { user: userWithoutPassword, token };
};

export const getUserProfile = async (userId) => {
    const user = await userModel.findUserById(userId);
    if (!user) {
        throw new Error("User not found");
    }
    return user;
};

export const updateUserProfile = async (userId, updates) => {
    const user = await userModel.updateUser(userId, updates);
    if (!user) {
        throw new Error("User not found");
    }
    return user;
};

export const updateOnlineStatus = async (userId, isOnline) => {
    return await userModel.updateOnlineStatus(userId, isOnline);
};

export const searchUsers = async (query, currentUserId) => {
    const users = await userModel.searchUsers(query, currentUserId);
    // Remove password from all users
    return users.map(({ password, ...rest }) => rest);
};

export const getAllUsers = async () => {
    const users = await userModel.getAllUsers();
    // Remove password from all users
    return users.map(({ password, ...rest }) => rest);
};

export const getUserById = async (userId) => {
    const user = await userModel.findUserById(userId);
    if (!user) {
        throw new Error("User not found");
    }
    return user;
};
