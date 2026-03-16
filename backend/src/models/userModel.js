import { pool as db } from "../config/db.js";

export const findByEmail = async (email) => {
    const [rows] = await db.query("SELECT * FROM users WHERE email = $1", [email]);
    return rows[0];
};

export const insert = async (user) => {
    const [result] = await db.query("INSERT INTO users (username, email, password) VALUES ($1, $2, $3)", [user.name, user.email, user.password]);
    const [rows] = await db.query("SELECT * FROM users WHERE id = $1", [result.insertId]);
    return rows[0];
};

export const update = async (user) => {
    await db.query("UPDATE users SET username = $1, password = $2 WHERE email = $3", [user.name, user.password, user.email]);
    const [rows] = await db.query("SELECT * FROM users WHERE email = $1", [user.email]);
    return rows[0];
};

export const deleteUser = async (email) => { // 'delete' is a reserved keyword, so changing to 'deleteUser'
    const [user] = await db.query("SELECT * FROM users WHERE email = $1", [email]);
    await db.query("DELETE FROM users WHERE email = $1", [email]);
    return user[0];
};

export const findAll = async () => {
    const [rows] = await db.query("SELECT * FROM users");
    return rows;
};



