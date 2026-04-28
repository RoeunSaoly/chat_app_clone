import app from "./src/app.js";
import dotenv from "dotenv";
import { connectDB } from "./src/config/db.js";
import http from "http";
import { initSocket } from "./src/config/socket.js";
import chatSocket from "./src/plugin/chat.Socket.js";
import { setIO } from "./src/utils/socketEmitter.js";

dotenv.config();

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

const PORT = process.env.PORT || 5000;

// start server
server.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});
