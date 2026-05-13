/**
 * Database migration to add search indexes for users table
 * This optimizes querying users by username and email
 */

export const up = async (client) => {
  // Create trigram extension for efficient ILIKE search (PostgreSQL specific)
  // Note: requires superuser, so we wrap in a try-catch or use standard b-tree if not available.
  // Standard b-tree indexes can't optimize '%search%' queries, but we can index username/email for prefix matching or exact matching.
  
  // Create b-tree indexes for exact matches
  await client.query(`CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);`);
  await client.query(`CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);`);

  // Try to create pg_trgm for full-text / ILIKE optimization if the DB supports it
  try {
    await client.query(`CREATE EXTENSION IF NOT EXISTS pg_trgm;`);
    await client.query(`CREATE INDEX IF NOT EXISTS idx_users_username_trgm ON users USING gin (username gin_trgm_ops);`);
    await client.query(`CREATE INDEX IF NOT EXISTS idx_users_email_trgm ON users USING gin (email gin_trgm_ops);`);
  } catch (error) {
    console.warn("Could not create pg_trgm extension or indexes. ILIKE searches will use standard seq scans.");
  }
};

export const down = async (client) => {
  await client.query(`DROP INDEX IF EXISTS idx_users_username_trgm;`);
  await client.query(`DROP INDEX IF EXISTS idx_users_email_trgm;`);
  await client.query(`DROP INDEX IF EXISTS idx_users_username;`);
  await client.query(`DROP INDEX IF EXISTS idx_users_email;`);
};
