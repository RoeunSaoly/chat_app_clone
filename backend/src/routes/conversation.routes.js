import express from "express";
import authMiddleware from "../middleware/auth.middleware.js";
import { validateRequest } from "../middleware/validate.middleware.js";
import {
  createGroupConversationSchema,
  createPrivateConversationSchema,
} from "../validations/conversation.validation.js";
import {
  getConversations,
  getConversation,
  createConversation,
  createGroupConversation,
  createPrivateConversation,
} from "../controllers/conversation.controller.js";

const router = express.Router();

router.use(authMiddleware);

router.get("/", getConversations);
router.get("/:id", getConversation);
router.post("/", createConversation);
router.post("/private", validateRequest(createPrivateConversationSchema), createPrivateConversation);
router.post("/group", validateRequest(createGroupConversationSchema), createGroupConversation);

export default router;
