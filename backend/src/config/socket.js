import { Server } from "socket.io";
import { createAdapter } from "@socket.io/redis-adapter";
import { pubClient, subClient } from "./redis.js";

export function initSocket(server) {

  const io = new Server(server, {
    cors: { origin: "*" }
  });

  io.adapter(createAdapter(pubClient, subClient));

  io.on("connection", (socket) => {

    console.log("User connected", socket.id);

    socket.on("join_room", (room) => {
      socket.join(room);
    });

    socket.on("send_message", (data) => {
      io.to(data.room).emit("receive_message", data);
    });

  });

}