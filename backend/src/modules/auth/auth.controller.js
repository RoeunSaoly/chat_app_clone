import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import * as authService from "./auth.service.js";
import { createUser, findUserByEmail, findUserByUsername } from "./auth.model.js";

export const register = async (req, res) => {
    try {
        const { username, email, password } = req.body;

        // Check if username already exists
        const existingUsername = await findUserByUsername(username);
        if (existingUsername) {
            return res.status(400).json({ error: "Username already taken" });
        }

        // Check if email already exists
        const existingEmail = await findUserByEmail(email);
        if (existingEmail) {
            return res.status(400).json({ error: "Email already registered" });
        }

        const hashedPassword = await bcrypt.hash(password, 10);

        await createUser(username, email, hashedPassword);

        res.json({
            message: "User registered successfully"
        });

    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

export const login = async (req, res) => {
    try {
        const { email, password } = req.body;
        const result = await authService.login(email, password);
        
        res.json({
            message: "Login successful",
            user: result.user,
            token: result.token
        });

    } catch (error) {
        res.status(401).json({ error: error.message });
    }
};