import app from "./src/app.js";
import dotenv from "dotenv";
import { connectDB } from "./src/config/db.js";
import http from "http";
import { Server } from "socket.io";
import chatSocket from "./src/sockets/chat.Socket.js";

dotenv.config();

// create http server
const server = http.createServer(app);

// socket.io
const io = new Server(server, {
  cors: {
    origin: "*",
  },
});

// register socket
chatSocket(io);

// connect database
connectDB();

const PORT = process.env.PORT || 3000;

// start server
server.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});