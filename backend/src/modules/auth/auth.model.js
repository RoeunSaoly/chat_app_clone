import { pool } from "../../config/db.js"

export const createUser = async (username, email, password) => {
    const [result] = await pool.query(
        "INSERT INTO users (username, email, password) VALUES (?, ?, ?)",
        [username, email, password]
    );

    return result;
};

export const findUserByEmail = async (email) => {
    const [rows] = await pool.query(
        "SELECT * FROM users WHERE email = ?",
        [email]
    );

    return rows[0];
};