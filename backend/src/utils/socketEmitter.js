/**
 * Socket.IO emitter utility
 * Holds the io instance for emitting events from anywhere in the app
 * Works with Redis adapter for multi-instance scaling
 */

let io = null;

export const setIO = (ioInstance) => {
  io = ioInstance;
};

export const getIO = () => {
  if (!io) {
    throw new Error("Socket.io not initialized. Call setIO first.");
  }
  return io;
};

/**
 * Emit an event to a specific user's room
 * @param {string|number} userId
 * @param {string} event
 * @param {*} data
 */
export const emitToUser = (userId, event, data) => {
  try {
    const ioInstance = getIO();
    ioInstance.to(`user:${userId}`).emit(event, data);
  } catch (error) {
    console.error("Socket emit error:", error.message);
  }
};

/**
 * Emit an event to a conversation room
 * @param {string|number} conversationId
 * @param {string} event
 * @param {*} data
 */
export const emitToConversation = (conversationId, event, data) => {
  try {
    const ioInstance = getIO();
    ioInstance.to(`conversation:${conversationId}`).emit(event, data);
  } catch (error) {
    console.error("Socket emit error:", error.message);
  }
};

