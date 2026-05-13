import app from "./src/app.js";
import dotenv from "dotenv";
import { connectDB } from "./src/config/db.js";
import http from "http";
import { initSocket } from "./src/config/socket.js";
import chatSocket from "./src/plugin/chat.Socket.js";
import { setIO } from "./src/utils/socketEmitter.js";
import { errorHandler } from "./src/middleware/error.middleware.js";

dotenv.config();

// Handle unhandled exceptions and rejections to prevent silent crashes
process.on('uncaughtException', (err) => {
  console.error('UNCAUGHT EXCEPTION!  Shutting down...');
  console.error(err.name, err.message, err.stack);
  process.exit(1);
});

process.on('unhandledRejection', (err) => {
  console.error('UNHANDLED REJECTION!  Shutting down...');
  console.error(err.name, err.message, err.stack);
  process.exit(1);
});

// create http server
const server = http.createServer(app);

// socket.io with Redis adapter for scaling
const io = initSocket(server);

// register socket emitter utility for cross-instance scaling
setIO(io);

// register socket plugins
chatSocket(io);

// connect database
connectDB();

const PORT = process.env.PORT || 3000;

// start server
server.listen(PORT, "0.0.0.0", () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
