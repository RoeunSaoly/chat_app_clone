/**
 * Add reply_to column to messages table
 */

export const up = async (client) => {
  await client.query(`
    ALTER TABLE messages 
    ADD COLUMN reply_to BIGINT NULL,
    ADD CONSTRAINT fk_message_reply 
    FOREIGN KEY (reply_to) 
    REFERENCES messages(id) ON DELETE SET NULL;
  `);
};

export const down = async (client) => {
  await client.query(`
    ALTER TABLE messages 
    DROP CONSTRAINT IF EXISTS fk_message_reply,
    DROP COLUMN IF EXISTS reply_to;
  `);
};
