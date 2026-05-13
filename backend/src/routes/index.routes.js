import express from "express";
import authRoutes from "../modules/auth/auth.routes.js";
import chatRoutes from "./chat.routes.js";
import userRoutes from "./user.routes.js";
import notificationRoutes from "../modules/notification/notification.routes.js";
import conversationRoutes from "./conversation.routes.js";
import messageRoutes from "./message.routes.js";
import authMiddleware from "../middleware/auth.middleware.js";

import { validateRequest } from "../middleware/validate.middleware.js";
import { updateTypingStatusSchema } from "../validations/message.validation.js";
import { updateTypingStatus } from "../controllers/message.controller.js";

const router = express.Router();

// API root
router.get("/", (req, res) => {
    res.json({
        message: "API working"
    });
});

// auth routes (register, login)
router.use("/auth", authRoutes);

// profile routes (get users, get profile)
router.use("/profile", userRoutes);
router.use("/users", userRoutes);

// refactored chat API routes
router.use("/conversations", conversationRoutes);
router.use("/messages", messageRoutes);
router.post("/typing", authMiddleware, validateRequest(updateTypingStatusSchema), updateTypingStatus);

// old routes
router.use("/chat", chatRoutes);

// notification routes
router.use("/notifications", notificationRoutes);

export default router;
