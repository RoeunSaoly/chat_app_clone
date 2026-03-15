import { pool as db } from "../config/db.js";

export const findByEmail = async (email) => {
    const [rows] = await db.query("SELECT * FROM users WHERE email = ?", [email]);
    return rows[0];
};

export const insert = async (user) => {
    const [result] = await db.query("INSERT INTO users (name, email, password) VALUES (?, ?, ?)", [user.name, user.email, user.password]);
    const [rows] = await db.query("SELECT * FROM users WHERE id = ?", [result.insertId]);
    return rows[0];
};

export const update = async (user) => {
    await db.query("UPDATE users SET name = ?, password = ? WHERE email = ?", [user.name, user.password, user.email]);
    const [rows] = await db.query("SELECT * FROM users WHERE email = ?", [user.email]);
    return rows[0];
};

export const deleteUser = async (email) => { // 'delete' is a reserved keyword, so changing to 'deleteUser'
    const [user] = await db.query("SELECT * FROM users WHERE email = ?", [email]);
    await db.query("DELETE FROM users WHERE email = ?", [email]);
    return user[0];
};

export const findAll = async () => {
    const [rows] = await db.query("SELECT * FROM users");
    return rows;
};



