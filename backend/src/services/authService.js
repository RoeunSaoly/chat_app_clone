import jwt from "jsonwebtoken";
import bcrypt from "bcryptjs";
import * as userModel from "../models/userModel.js";

export const login = async (email, password) => {
    const user = await userModel.findByEmail(email);
    if (!user) {
        throw new Error("User not found");
    }
    const valid = await bcrypt.compare(password, user.password);
    if (!valid) throw new Error("Invalid password");

    const token = jwt.sign(
        { id: user.id, email: user.email },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRE }
    );

    return { user, token };

}



