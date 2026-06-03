/**
 * Migration: Add is_edited column to messages table
 */

export const up = async (client) => {
  await client.query(`
    ALTER TABLE messages 
    ADD COLUMN IF NOT EXISTS is_edited BOOLEAN DEFAULT FALSE;
  `);
};

export const down = async (client) => {
  await client.query(`
    ALTER TABLE messages 
    DROP COLUMN IF EXISTS is_edited;
  `);
};
