import jwt from "jsonwebtoken";
import bcrypt from "bcryptjs";
import * as userModel from "./auth.model.js";

const generateTokens = (user) => {
    const jwtSecret = process.env.JWT_SECRET;
    const jwtRefreshSecret = process.env.JWT_REFRESH_SECRET || 'refresh_secret_change_me';

    if (!jwtSecret) {
        console.error("CRITICAL: JWT_SECRET is not defined in environment variables");
        throw new Error("Server configuration error");
    }

    const accessToken = jwt.sign(
        { id: user.id, email: user.email },
        jwtSecret,
        { expiresIn: '15m' }
    );

    const refreshToken = jwt.sign(
        { id: user.id },
        jwtRefreshSecret,
        { expiresIn: '7d' }
    );

    return { accessToken, refreshToken };
};

export const login = async (email, password) => {
    try {
        const user = await userModel.findUserByEmail(email);
        if (!user) {
            throw new Error("Invalid credentials");
        }

        const valid = await bcrypt.compare(password, user.password);
        if (!valid) throw new Error("Invalid credentials");

        const tokens = generateTokens(user);

        // Remove password from user object
        const { password: _, ...userWithoutPassword } = user;
        return { user: userWithoutPassword, ...tokens };
    } catch (error) {
        console.error("Service Login Error:", error.message);
        throw error;
    }
};

export const refreshSession = async (refreshToken) => {
    try {
        const decoded = jwt.verify(refreshToken, process.env.JWT_REFRESH_SECRET || 'refresh_secret_change_me');
        const user = await userModel.getUserById(decoded.id);
        
        if (!user) throw new Error('User no longer exists');
        
        return generateTokens(user);
    } catch (err) {
        throw new Error('Invalid refresh token');
    }
};
