import bcrypt from "bcryptjs";
import * as authService from "./auth.service.js";
import { createUser, findUserByEmail, findUserByUsername } from "./auth.model.js";

export const register = async (req, res) => {
    try {
        const { username, email, password } = req.body;
        const trimmedUsername = username?.trim();
        const trimmedEmail = email?.trim().toLowerCase();

        if (!trimmedUsername || !trimmedEmail || !password) {
            return res.status(400).json({ success: false, error: "username, email and password are required" });
        }

        if (password.length < 6) {
            return res.status(400).json({ success: false, error: "Password must be at least 6 characters" });
        }

        // Check if username already exists
        const existingUsername = await findUserByUsername(trimmedUsername);
        if (existingUsername) {
            return res.status(409).json({ success: false, error: "Username already taken" });
        }

        // Check if email already exists
        const existingEmail = await findUserByEmail(trimmedEmail);
        if (existingEmail) {
            return res.status(409).json({ success: false, error: "Email already registered" });
        }

        const hashedPassword = await bcrypt.hash(password, 10);

        const user = await createUser(trimmedUsername, trimmedEmail, hashedPassword);

        res.status(201).json({
            success: true,
            message: "User registered successfully",
            user
        });

    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

export const login = async (req, res) => {
    try {
        const { email, password } = req.body;
        if (!email || !password) {
            return res.status(400).json({ success: false, error: "email and password are required" });
        }

        const result = await authService.login(email.trim().toLowerCase(), password);
        
        res.json({
            success: true,
            message: "Login successful",
            user: result.user,
            token: result.token
        });

    } catch (error) {
        res.status(401).json({ success: false, error: error.message });
    }
};
