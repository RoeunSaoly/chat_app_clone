import jwt from "jsonwebtoken";
import bcrypt from "bcryptjs";
import * as userModel from "./auth.model.js";

export const login = async (email, password) => {
    const user = await userModel.findUserByEmail(email);
    if (!user) {
        throw new Error("User not found");
    }
    const valid = await bcrypt.compare(password, user.password);
    if (!valid) throw new Error("Invalid password");

    const token = jwt.sign(
        { id: user.id, email: user.email },
        process.env.JWT_SECRET || "dev_secret_change_me",
        { expiresIn: process.env.JWT_EXPIRE || "1d" }
    );

    // Remove password from user object before returning
    const { password: _, ...userWithoutPassword } = user;
    return { user: userWithoutPassword, token };

}
