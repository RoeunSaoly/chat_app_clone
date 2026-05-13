import express from "express";
import authMiddleware from "../middleware/auth.middleware.js";
import { validateRequest } from "../middleware/validate.middleware.js";
import {
  sendMessageSchema,
  markMessagesSeenSchema,
  updateTypingStatusSchema,
  reactToMessageSchema,
  editMessageSchema,
} from "../validations/message.validation.js";
import {
  getMessages,
  markMessagesSeen,
  reactToMessage,
  sendMessage,
  updateTypingStatus,
  deleteMessage,
} from "../controllers/message.controller.js";

const router = express.Router();

router.use(authMiddleware);

router.get("/:conversationId", getMessages);
router.post("/", validateRequest(sendMessageSchema), sendMessage);
router.post("/seen", validateRequest(markMessagesSeenSchema), markMessagesSeen);
router.post("/react", validateRequest(reactToMessageSchema), reactToMessage);
router.patch("/:messageId", validateRequest(editMessageSchema), editMessage);
router.delete("/:messageId", deleteMessage);

export default router;
