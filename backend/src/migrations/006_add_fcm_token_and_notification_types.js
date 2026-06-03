/**
 * Migration: Add FCM token to users and friend_request to notification types
 */

export const up = async (client) => {
  // Add fcm_token to users table
  await client.query(`
    ALTER TABLE users ADD COLUMN IF NOT EXISTS fcm_token VARCHAR(255);
  `);

  // Update notifications type check constraint
  // To do this in Postgres, we drop the constraint and recreate it
  // First, find the constraint name. It's usually notifications_type_check.
  try {
    await client.query(`
      ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_type_check;
    `);
  } catch (e) {
    console.log("Could not drop constraint, it might not exist or have a different name.", e.message);
  }

  // Also add friend_request and friend_accepted types
  await client.query(`
    ALTER TABLE notifications ADD CONSTRAINT notifications_type_check 
    CHECK (type IN ('message', 'system', 'friend_request', 'friend_accepted'));
  `);
};

export const down = async (client) => {
  await client.query(`
    ALTER TABLE users DROP COLUMN IF EXISTS fcm_token;
  `);

  try {
    await client.query(`
      ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_type_check;
    `);
  } catch (e) {}

  await client.query(`
    ALTER TABLE notifications ADD CONSTRAINT notifications_type_check 
    CHECK (type IN ('message', 'system'));
  `);
};
