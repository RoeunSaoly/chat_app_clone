import jwt from "jsonwebtoken";
import * as messageService from "../modules/message/message.service.js";
import * as userService from "../modules/user/user.service.js";
import { emitToConversation, emitToUser } from "../utils/socketEmitter.js";

// In-memory tracking of online users (can be replaced with Redis for scaling)
const onlineUsers = new Map();

const chatSocket = (io) => {

  // middleware authentication
  io.use((socket, next) => {
    const token = socket.handshake.auth.token;

    if (!token) {
      return next(new Error("Authentication error: No token provided"));
    }

    try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      socket.user = decoded;
      next();
    } catch (err) {
      next(new Error("Authentication error: Invalid token"));
    }
  });

  // connection
  io.on("connection", async (socket) => {
    const userId = socket.user.id;
    console.log("User connected:", userId);

    // Track online status
    onlineUsers.set(userId.toString(), socket.id);
    await userService.updateOnlineStatus(userId, true);

    // Join personal room for targeted notifications
    socket.join(`user:${userId}`);

    // Emit user_online event to all connected sockets
    socket.broadcast.emit("user_online", { user_id: userId });

    // Join conversation rooms on request
    socket.on("join_conversation", (conversationId) => {
      const room = `conversation:${conversationId}`;
      socket.join(room);
      console.log(`User ${userId} joined room ${room}`);
    });

    // Leave conversation room
    socket.on("leave_conversation", (conversationId) => {
      const room = `conversation:${conversationId}`;
      socket.leave(room);
      console.log(`User ${userId} left room ${room}`);
    });

    // Handle send_message event
    socket.on("send_message", async (data) => {
      try {
        const { conversation_id, content, message_type = "text" } = data;

        if (!conversation_id || !content) {
          socket.emit("error", { message: "conversation_id and content are required" });
          return;
        }

        const result = await messageService.handleSendMessageViaSocket(
          userId,
          conversation_id,
          content,
          message_type
        );

        // The service already emits new_message to the conversation room
        // and message_delivered to the sender
      } catch (error) {
        console.error("send_message error:", error.message);
        socket.emit("error", { message: error.message });
      }
    });

    // Handle typing indicator
    socket.on("typing", async (data) => {
      try {
        const { conversation_id } = data;
        if (!conversation_id) return;

        await messageService.handleTyping(conversation_id, userId, true);
      } catch (error) {
        console.error("typing error:", error.message);
      }
    });

    // Handle stop typing indicator
    socket.on("stop_typing", async (data) => {
      try {
        const { conversation_id } = data;
        if (!conversation_id) return;

        await messageService.handleTyping(conversation_id, userId, false);
      } catch (error) {
        console.error("stop_typing error:", error.message);
      }
    });

    // Handle message seen
    socket.on("message_seen", async (data) => {
      try {
        const { message_id } = data;
        if (!message_id) return;

        await messageService.handleMessageSeen(message_id, userId);
      } catch (error) {
        console.error("message_seen error:", error.message);
      }
    });

    // Handle mark all messages as seen in conversation
    socket.on("mark_all_seen", async (data) => {
      try {
        const { conversation_id } = data;
        if (!conversation_id) return;

        await messageService.markAllMessagesAsSeen(conversation_id, userId);
      } catch (error) {
        console.error("mark_all_seen error:", error.message);
      }
    });

    // Handle disconnect
    socket.on("disconnect", async () => {
      console.log("User disconnected:", userId);

      // Remove from online tracking
      onlineUsers.delete(userId.toString());

      // Update database status
      await userService.updateOnlineStatus(userId, false);

      // Emit user_offline event
      socket.broadcast.emit("user_offline", { user_id: userId, last_seen: new Date().toISOString() });
    });
  });

};

export default chatSocket;
