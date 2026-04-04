import { pool } from "../../config/db.js";

// GET USER
export const findUserById = async (id) => {
  const result = await pool.query(
    `SELECT id, username, email, avatar, status_message 
     FROM users WHERE id = $1`,
    [id]
  );
  return result.rows[0];
};

// UPDATE USER
export const updateUser = async (id, data) => {
  const { username, avatar, status_message } = data;

  const result = await pool.query(
    `UPDATE users
     SET username = $1,
         avatar = $2,
         status_message = $3
     WHERE id = $4
     RETURNING id, username, email, avatar, status_message`,
    [username, avatar, status_message, id]
  );

  return result.rows[0];
};

// DELETE USER
export const deleteUser = async (id) => {
  await pool.query(`DELETE FROM users WHERE id = $1`, [id]);
};