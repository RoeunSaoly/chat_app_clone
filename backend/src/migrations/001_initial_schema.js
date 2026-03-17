/**
 * Initial database schema migration
 * Creates all required tables for the chat application
 */

export const up = async (client) => {
  // Create users table
  await client.query(`
    CREATE TABLE IF NOT EXISTS users (
      id BIGSERIAL PRIMARY KEY,
      username VARCHAR(100) NOT NULL,
      email VARCHAR(150) UNIQUE NOT NULL,
      password VARCHAR(255) NOT NULL,
      avatar VARCHAR(255),
      status_message VARCHAR(255),
      is_online BOOLEAN DEFAULT FALSE,
      last_seen TIMESTAMP NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `);

  // Create conversations table
  await client.query(`
    CREATE TABLE IF NOT EXISTS conversations (
      id BIGSERIAL PRIMARY KEY,
      type VARCHAR(10) NOT NULL CHECK (type IN ('private', 'group')),
      name VARCHAR(150),
      avatar VARCHAR(255),
      created_by BIGINT,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (created_by) REFERENCES users(id)
    );
  `);

  // Create conversation_members table
  await client.query(`
    CREATE TABLE IF NOT EXISTS conversation_members (
      id BIGSERIAL PRIMARY KEY,
      conversation_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      role VARCHAR(10) DEFAULT 'member' CHECK (role IN ('admin', 'member')),
      joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(conversation_id, user_id),
      FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
  `);

  // Create messages table
  await client.query(`
    CREATE TABLE IF NOT EXISTS messages (
      id BIGSERIAL PRIMARY KEY,
      conversation_id BIGINT NOT NULL,
      sender_id BIGINT NOT NULL,
      message_type VARCHAR(10) DEFAULT 'text' CHECK (message_type IN ('text', 'image', 'file', 'video', 'audio')),
      content TEXT,
      attachment_url VARCHAR(255),
      status VARCHAR(10) DEFAULT 'sending' CHECK (status IN ('sending', 'sent', 'delivered', 'seen')),
      deleted_for_everyone BOOLEAN DEFAULT FALSE,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
      FOREIGN KEY (sender_id) REFERENCES users(id)
    );
  `);

  // Create message_reads table
  await client.query(`
    CREATE TABLE IF NOT EXISTS message_reads (
      id BIGSERIAL PRIMARY KEY,
      message_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(message_id, user_id),
      FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id)
    );
  `);

  // Create message_deleted_for_users table
  await client.query(`
    CREATE TABLE IF NOT EXISTS message_deleted_for_users (
      id BIGSERIAL PRIMARY KEY,
      message_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(message_id, user_id),
      FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id)
    );
  `);

  // Create conversation_deleted_for_users table
  await client.query(`
    CREATE TABLE IF NOT EXISTS conversation_deleted_for_users (
      id BIGSERIAL PRIMARY KEY,
      conversation_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(conversation_id, user_id),
      FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id)
    );
  `);

  // Create typing_status table
  await client.query(`
    CREATE TABLE IF NOT EXISTS typing_status (
      id BIGSERIAL PRIMARY KEY,
      conversation_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      is_typing BOOLEAN DEFAULT FALSE,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(conversation_id, user_id),
      FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id)
    );
  `);

  // Create message_reactions table
  await client.query(`
    CREATE TABLE IF NOT EXISTS message_reactions (
      id BIGSERIAL PRIMARY KEY,
      message_id BIGINT NOT NULL,
      user_id BIGINT NOT NULL,
      reaction VARCHAR(20),
      UNIQUE(message_id, user_id),
      FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id)
    );
  `);

  // Create indexes
  await client.query(`CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id);`);
  await client.query(`CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender_id);`);
  await client.query(`CREATE INDEX IF NOT EXISTS idx_conversation_members_user ON conversation_members(user_id);`);
  await client.query(`CREATE INDEX IF NOT EXISTS idx_message_reads_message ON message_reads(message_id);`);
};

export const down = async (client) => {
  // Drop indexes
  await client.query(`DROP INDEX IF EXISTS idx_message_reads_message;`);
  await client.query(`DROP INDEX IF EXISTS idx_conversation_members_user;`);
  await client.query(`DROP INDEX IF EXISTS idx_messages_sender;`);
  await client.query(`DROP INDEX IF EXISTS idx_messages_conversation;`);

  // Drop tables in reverse order of dependencies
  await client.query(`DROP TABLE IF EXISTS message_reactions;`);
  await client.query(`DROP TABLE IF EXISTS typing_status;`);
  await client.query(`DROP TABLE IF EXISTS conversation_deleted_for_users;`);
  await client.query(`DROP TABLE IF EXISTS message_deleted_for_users;`);
  await client.query(`DROP TABLE IF EXISTS message_reads;`);
  await client.query(`DROP TABLE IF EXISTS messages;`);
  await client.query(`DROP TABLE IF EXISTS conversation_members;`);
  await client.query(`DROP TABLE IF EXISTS conversations;`);
  await client.query(`DROP TABLE IF EXISTS users;`);
};
