import { pool } from "../config/db.js";

const rollbackLastMigration = async () => {
  const client = await pool.connect();

  try {
    console.log(" Rolling back last migration...");

    await client.query("BEGIN");

    // Get last migration
    const { rows } = await client.query(`
      SELECT name FROM migrations
      ORDER BY id DESC
      LIMIT 1
    `);

    if (rows.length === 0) {
      console.log("No migrations to rollback");
      return;
    }

    const last = rows[0].name;

    console.log(` Reverting ${last}`);

    const migration = await import(`../migrations/${last}`);
    await migration.down(client);

    await client.query(
      "DELETE FROM migrations WHERE name = $1",
      [last]
    );

    await client.query("COMMIT");

    console.log(" Rollback successful");
  } catch (error) {
    await client.query("ROLLBACK");
    console.error(" Rollback failed:", error.message);
  } finally {
    client.release();
    process.exit();
  }
};

rollbackLastMigration();