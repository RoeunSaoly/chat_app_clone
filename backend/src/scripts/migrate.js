import { pool } from "../config/db.js";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const runMigrations = async () => {
  const client = await pool.connect();

  try {
    console.log(" Running auto migrations...");

    await client.query("BEGIN");

    // 1. Create migrations table
    await client.query(`
      CREATE TABLE IF NOT EXISTS migrations (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) UNIQUE,
        run_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    `);

    // 2. Get executed migrations
    const { rows } = await client.query("SELECT name FROM migrations");
    const executed = rows.map((r) => r.name);

    // 3. Read migration files
    const migrationsPath = path.join(__dirname, "../migrations");
    const files = fs.readdirSync(migrationsPath).sort();

    for (const file of files) {
      if (executed.includes(file)) {
        console.log(` Skipping ${file}`);
        continue;
      }

      console.log(` Running ${file}`);

      const migration = await import(`../migrations/${file}`);
      await migration.up(client);

      await client.query(
        "INSERT INTO migrations(name) VALUES($1)",
        [file]
      );
    }

    await client.query("COMMIT");

    console.log(" All migrations executed");
  } catch (error) {
    await client.query("ROLLBACK");
    console.error(" Migration error:", error.message);
  } finally {
    client.release();
    process.exit();
  }
};

runMigrations();