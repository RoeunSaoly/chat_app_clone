import mysql from 'mysql2/promise';

const pool = mysql.createPool({

    host: procces.env.DB_HOST,
    user: procces.env.DB_USER,
    password: procces.env.DB_PASSWORD,
    database: procces.env.DB_NAME,
    waitForConnections: true,
    connectionLimit: 10,
});
export default pool;