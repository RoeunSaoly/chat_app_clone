import { Server } from "socket.io";
import { createAdapter } from "@socket.io/redis-adapter";
import { pubClient, subClient } from "./redis.js";

/**
 * Initialize Socket.IO server with Redis adapter for horizontal scaling.
 * Connection and event handlers are registered separately in chat.Socket.js.
 */
export function initSocket(server) {
  const io = new Server(server, {
    cors: { origin: "*" }
  });

  io.adapter(createAdapter(pubClient, subClient));

  return io;
}
