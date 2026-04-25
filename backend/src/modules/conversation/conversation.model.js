import { pool } from "../../config/db.js";

/**
 * Get all conversations for a user with last message preview and unread count
 */
export const getConversationsByUser = async (userId) => {
  const result = await pool.query(
    `SELECT
       c.id,
       c.type,
       c.name,
       c.avatar,
       c.created_at,
       (
         SELECT m.content
         FROM messages m
         WHERE m.conversation_id = c.id
         ORDER BY m.created_at DESC
         LIMIT 1
       ) as last_message,
       (
         SELECT m.created_at
         FROM messages m
         WHERE m.conversation_id = c.id
         ORDER BY m.created_at DESC
         LIMIT 1
       ) as last_message_at,
       (
         SELECT COUNT(*)::int
         FROM messages m
         WHERE m.conversation_id = c.id
           AND m.sender_id != $1
           AND NOT EXISTS (
             SELECT 1 FROM message_reads mr
             WHERE mr.message_id = m.id AND mr.user_id = $1
           )
       ) as unread_count
     FROM conversations c
     JOIN conversation_members cm ON cm.conversation_id = c.id
     WHERE cm.user_id = $1
     ORDER BY last_message_at DESC NULLS LAST`,
    [userId]
  );

  return result.rows;
};

/**
 * Get conversation members with user details
 */
export const getConversationMembers = async (conversationId) => {
  const result = await pool.query(
    `SELECT cm.user_id, cm.role, cm.joined_at,
            u.username, u.avatar, u.is_online, u.last_seen
     FROM conversation_members cm
     JOIN users u ON u.id = cm.user_id
     WHERE cm.conversation_id = $1`,
    [conversationId]
  );

  return result.rows;
};

/**
 * Get a single conversation by ID
 */
export const getConversationById = async (conversationId) => {
  const result = await pool.query(
    `SELECT id, type, name, avatar, created_by, created_at
     FROM conversations WHERE id = $1`,
    [conversationId]
  );

  return result.rows[0];
};

/**
 * Create a new conversation
 */
export const createConversation = async (data) => {
  const { type, name, avatar, created_by } = data;

  const result = await pool.query(
    `INSERT INTO conversations (type, name, avatar, created_by)
     VALUES ($1, $2, $3, $4)
     RETURNING id, type, name, avatar, created_by, created_at`,
    [type, name, avatar, created_by]
  );

  return result.rows[0];
};

/**
 * Add members to a conversation
 */
export const addConversationMembers = async (conversationId, memberIds) => {
  if (!memberIds || memberIds.length === 0) return [];

  const values = memberIds
    .map((_, i) => `($1, $${i + 2})`)
    .join(", ");

  const result = await pool.query(
    `INSERT INTO conversation_members (conversation_id, user_id)
     VALUES ${values}
     ON CONFLICT (conversation_id, user_id) DO NOTHING
     RETURNING id, conversation_id, user_id, role, joined_at`,
    [conversationId, ...memberIds]
  );

  return result.rows;
};

/**
 * Check if user is a member of a conversation
 */
export const isConversationMember = async (conversationId, userId) => {
  const result = await pool.query(
    `SELECT 1 FROM conversation_members
     WHERE conversation_id = $1 AND user_id = $2`,
    [conversationId, userId]
  );

  return result.rowCount > 0;
};

