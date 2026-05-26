/**
 * Migration: Add friends table for user relationships
 */

export const up = async (client) => {
  // Create friends table to track friend relationships
  await client.query(`
    CREATE TABLE IF NOT EXISTS friends (
      id BIGSERIAL PRIMARY KEY,
      user_id BIGINT NOT NULL,
      friend_id BIGINT NOT NULL,
      status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'blocked')),
      requested_by BIGINT NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      UNIQUE(user_id, friend_id),
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
      FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
      FOREIGN KEY (requested_by) REFERENCES users(id) ON DELETE CASCADE
    );
  `);

  // Create index for faster lookups
  await client.query(`
    CREATE INDEX IF NOT EXISTS idx_friends_user_id ON friends(user_id);
  `);
  await client.query(`
    CREATE INDEX IF NOT EXISTS idx_friends_friend_id ON friends(friend_id);
  `);
  await client.query(`
    CREATE INDEX IF NOT EXISTS idx_friends_status ON friends(status);
  `);
};

export const down = async (client) => {
  // Drop friends table
  await client.query(`DROP TABLE IF NOT EXISTS friends;`);
};
