const express = require("express");
const router = express.Router();

const authMiddleware = require("../middleware/auth.middleware");
const chatController = require("../controllers/chatController");
router.get("/rooms", authMiddleware, chatController.getRooms);

router.get("/messages/:roomId", authMiddleware, chatController.getMessages);

module.exports = router;

