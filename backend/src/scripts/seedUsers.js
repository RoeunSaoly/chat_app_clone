import bcrypt from "bcryptjs";
import { pool } from "../../config/db.js";

const seedUsers = async () => {
    try {
        console.log("Seeding 10 sample users...");
        const passwordHash = await bcrypt.hash("12345678", 10);
        
        for (let i = 1; i <= 10; i++) {
            const username = `testuser${i}`;
            const email = `testuser${i}@example.com`;
            
            // Check if user exists
            const checkResult = await pool.query("SELECT id FROM users WHERE email = $1", [email]);
            if (checkResult.rows.length === 0) {
                await pool.query(
                    `INSERT INTO users (username, email, password) VALUES ($1, $2, $3)`,
                    [username, email, passwordHash]
                );
                console.log(`Created user: ${username} (${email})`);
            } else {
                console.log(`User already exists: ${username} (${email})`);
            }
        }
        
        console.log("Seeding completed successfully!");
    } catch (error) {
        console.error("Error seeding users:", error);
    } finally {
        pool.end();
    }
};

seedUsers();
