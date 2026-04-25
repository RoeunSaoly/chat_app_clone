/**
 * Notification system migration
 * Creates notifications table with indexes
 */

export const up = async (client) => {
  // Create notifications table
  await client.query(`
    CREATE TABLE IF NOT EXISTS notifications (
      id BIGSERIAL PRIMARY KEY,
      user_id BIGINT NOT NULL,
      type VARCHAR(20) NOT NULL CHECK (type IN ('message', 'system')),
      title VARCHAR(255) NOT NULL,
      content TEXT,
      is_read BOOLEAN DEFAULT FALSE,
      is_deleted BOOLEAN DEFAULT FALSE,
      related_id BIGINT,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
  `);

  // Create indexes
  await client.query(`CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);`);
  await client.query(`CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(is_read);`);
  await client.query(`CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at DESC);`);
};

export const down = async (client) => {
  // Drop indexes
  await client.query(`DROP INDEX IF EXISTS idx_notifications_created_at;`);
  await client.query(`DROP INDEX IF EXISTS idx_notifications_is_read;`);
  await client.query(`DROP INDEX IF EXISTS idx_notifications_user_id;`);

  // Drop table
  await client.query(`DROP TABLE IF EXISTS notifications;`);
};

