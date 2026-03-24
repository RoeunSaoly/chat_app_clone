import express from "express";
import authRoutes from "../modules/auth/auth.routes.js";
import chatRoutes from "./chat.routes.js";
import userRoutes from "../modules/user/user.routes.js";
const router = express.Router();

// API root
router.get("/", (req, res) => {
    res.json({
        message: "API working"
    });
});

// auth routes
router.use("/auth", authRoutes);

// user profile
router.use("/profile", userRoutes);

// chat routes
router.use("/chat", chatRoutes);

export default router;