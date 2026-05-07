import { pool } from "../../config/db.js";

/**
 * Create a new message
 */
export const createMessage = async (data) => {
  const { conversation_id, sender_id, content, message_type = "text" } = data;

  const result = await pool.query(
    `INSERT INTO messages (conversation_id, sender_id, content, message_type, status)
     VALUES ($1, $2, $3, $4, 'sent')
     RETURNING id, conversation_id, sender_id, content, message_type, status, created_at`,
    [conversation_id, sender_id, content, message_type]
  );

  return result.rows[0];
};

/**
 * Get conversation members excluding a specific user
 */
export const getConversationMembers = async (conversationId, excludeUserId) => {
  const result = await pool.query(
    `SELECT cm.user_id, u.username
     FROM conversation_members cm
     JOIN users u ON u.id = cm.user_id
     WHERE cm.conversation_id = $1 AND cm.user_id != $2`,
    [conversationId, excludeUserId]
  );

  return result.rows;
};

/**
 * Get a single message by ID
 */
export const getMessageById = async (messageId) => {
  const result = await pool.query(
    `SELECT m.id, m.conversation_id, m.sender_id, m.content, m.message_type, m.status, m.created_at,
            u.username as sender_username
     FROM messages m
     JOIN users u ON u.id = m.sender_id
     WHERE m.id = $1`,
    [messageId]
  );

  return result.rows[0];
};

/**
 * Get sender username by ID
 */
export const getSenderUsername = async (userId) => {
  const result = await pool.query(
    `SELECT username FROM users WHERE id = $1`,
    [userId]
  );

  return result.rows[0]?.username;
};

/**
 * Get paginated messages for a conversation with sender info
 */
export const getMessagesByConversation = async (conversationId, limit = 50, offset = 0) => {
  const result = await pool.query(
    `SELECT m.id, m.conversation_id, m.sender_id, m.content, m.message_type, m.status, m.created_at,
            u.username as sender_username, u.avatar as sender_avatar
     FROM messages m
     JOIN users u ON u.id = m.sender_id
     WHERE m.conversation_id = $1
     ORDER BY m.created_at ASC
     LIMIT $2 OFFSET $3`,
    [conversationId, limit, offset]
  );

  return result.rows;
};

/**
 * Get read receipts for a specific message
 */
export const getMessageReads = async (messageId) => {
  const result = await pool.query(
    `SELECT mr.user_id, mr.seen_at, u.username
     FROM message_reads mr
     JOIN users u ON u.id = mr.user_id
     WHERE mr.message_id = $1`,
    [messageId]
  );

  return result.rows;
};

/**
 * Bulk insert message_reads entries for delivered status
 */
export const createMessageReads = async (messageId, userIds) => {
  if (!userIds || userIds.length === 0) return [];

  const values = userIds.map((_, i) => `($1, $${i + 2})`).join(", ");
  const result = await pool.query(
    `INSERT INTO message_reads (message_id, user_id)
     VALUES ${values}
     ON CONFLICT (message_id, user_id) DO NOTHING
     RETURNING id, message_id, user_id, seen_at`,
    [messageId, ...userIds]
  );

  return result.rows;
};

/**
 * Mark a specific message as seen by a user
 */
export const markMessageAsSeen = async (messageId, userId) => {
  const result = await pool.query(
    `INSERT INTO message_reads (message_id, user_id)
     VALUES ($1, $2)
     ON CONFLICT (message_id, user_id) DO UPDATE SET seen_at = CURRENT_TIMESTAMP
     RETURNING id, message_id, user_id, seen_at`,
    [messageId, userId]
  );

  return result.rows[0];
};

/**
 * Mark all unread messages in a conversation as seen by a user
 */
export const markMessagesAsSeen = async (conversationId, userId) => {
  const result = await pool.query(
    `INSERT INTO message_reads (message_id, user_id)
     SELECT m.id, $2
     FROM messages m
     WHERE m.conversation_id = $1
       AND m.sender_id != $2
       AND NOT EXISTS (
         SELECT 1 FROM message_reads mr
         WHERE mr.message_id = m.id AND mr.user_id = $2
       )
     RETURNING message_id`,
    [conversationId, userId]
  );

  return result.rows.map((r) => r.message_id);
};

/**
 * Update message status
 */
export const updateMessageStatus = async (messageId, status) => {
  const result = await pool.query(
    `UPDATE messages SET status = $2 WHERE id = $1 RETURNING id, status`,
    [messageId, status]
  );

  return result.rows[0];
};

/**
 * Update status for multiple messages.
 */
export const updateMessagesStatus = async (messageIds, status) => {
  if (!messageIds || messageIds.length === 0) return [];

  const result = await pool.query(
    `UPDATE messages
     SET status = $2
     WHERE id = ANY($1::bigint[])
     RETURNING id, status`,
    [messageIds, status]
  );

  return result.rows;
};

/**
 * Get unread message count for a user in a conversation
 */
export const getConversationUnreadCount = async (conversationId, userId) => {
  const result = await pool.query(
    `SELECT COUNT(*)::int as count
     FROM messages m
     WHERE m.conversation_id = $1
       AND m.sender_id != $2
       AND NOT EXISTS (
         SELECT 1 FROM message_reads mr
         WHERE mr.message_id = m.id AND mr.user_id = $2
       )`,
    [conversationId, userId]
  );

  return result.rows[0]?.count || 0;
};

/**
 * Update typing status for a user in a conversation
 */
export const updateTypingStatus = async (conversationId, userId, isTyping) => {
  const result = await pool.query(
    `INSERT INTO typing_status (conversation_id, user_id, is_typing)
     VALUES ($1, $2, $3)
     ON CONFLICT (conversation_id, user_id)
     DO UPDATE SET is_typing = $3, updated_at = CURRENT_TIMESTAMP
     RETURNING id, conversation_id, user_id, is_typing, updated_at`,
    [conversationId, userId, isTyping]
  );

  return result.rows[0];
};

/**
 * Get typing users in a conversation (excluding a specific user)
 */
export const getTypingUsers = async (conversationId, excludeUserId) => {
  const result = await pool.query(
    `SELECT ts.user_id, u.username
     FROM typing_status ts
     JOIN users u ON u.id = ts.user_id
     WHERE ts.conversation_id = $1
       AND ts.user_id != $2
       AND ts.is_typing = TRUE
       AND ts.updated_at > NOW() - INTERVAL '5 seconds'`,
    [conversationId, excludeUserId]
  );

  return result.rows;
};

/**
 * Add or update a user's reaction for a message.
 */
export const upsertReaction = async (messageId, userId, reaction) => {
  const result = await pool.query(
    `INSERT INTO message_reactions (message_id, user_id, reaction)
     VALUES ($1, $2, $3)
     ON CONFLICT (message_id, user_id)
     DO UPDATE SET reaction = $3
     RETURNING id, message_id, user_id, reaction`,
    [messageId, userId, reaction]
  );

  return result.rows[0];
};

/**
 * Remove a user's reaction from a message.
 */
export const deleteReaction = async (messageId, userId) => {
  const result = await pool.query(
    `DELETE FROM message_reactions
     WHERE message_id = $1 AND user_id = $2
     RETURNING id, message_id, user_id, reaction`,
    [messageId, userId]
  );

  return result.rows[0] || null;
};

/**
 * Get all reactions for a message.
 */
export const getMessageReactions = async (messageId) => {
  const result = await pool.query(
    `SELECT mr.user_id, mr.reaction, u.username
     FROM message_reactions mr
     JOIN users u ON u.id = mr.user_id
     WHERE mr.message_id = $1
     ORDER BY mr.id ASC`,
    [messageId]
  );

  return result.rows;
};

