import { z } from "zod";
import bcrypt from "bcryptjs";
import * as authService from "./auth.service.js";
import { createUser, findUserByEmail, findUserByUsername } from "./auth.model.js";

const registerSchema = z.object({
  username: z.string().min(3).max(30),
  email: z.string().email(),
  password: z.string().min(6),
});

const loginSchema = z.object({
  email: z.string().email(),
  password: z.string(),
});

export const register = async (req, res, next) => {
  try {
    const { username, email, password } = registerSchema.parse(req.body);

    const existingUsername = await findUserByUsername(username);
    if (existingUsername) return res.status(409).json({ success: false, error: "Username taken" });

    const existingEmail = await findUserByEmail(email);
    if (existingEmail) return res.status(409).json({ success: false, error: "Email registered" });

    const hashedPassword = await bcrypt.hash(password, 10);
    const user = await createUser(username, email.toLowerCase(), hashedPassword);

    res.status(201).json({ success: true, user });
  } catch (error) {
    console.error("Register Error:", error);
    next(error);
  }
};

export const login = async (req, res, next) => {
  try {
    const { email, password } = loginSchema.parse(req.body);
    const result = await authService.login(email.toLowerCase(), password);

    res.json({
      success: true,
      ...result
    });
  } catch (error) {
    console.error("Login Error:", error);
    next(error);
  }
};

export const refresh = async (req, res) => {
  const { refreshToken } = req.body;
  if (!refreshToken) return res.status(400).json({ error: "Refresh token required" });

  const result = await authService.refreshSession(refreshToken);
  res.json({ success: true, ...result });
};

