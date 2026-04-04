import jwt from "jsonwebtoken";

const chatSocket = (io) => {

  // middleware authentication
  io.use((socket, next) => {
    const token = socket.handshake.auth.token;

    try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      socket.user = decoded;
      next();
    } catch (err) {
      next(new Error("Authentication error"));
    }
  });

  // connection
  io.on("connection", (socket) => {
    console.log("User connected:", socket.user.id);

    socket.on("sendMessage", (data) => {
      io.to(data.roomId).emit("newMessage", data);
    });

    socket.on("disconnect", () => {
      console.log("User disconnected:", socket.user.id);
    });
  });

};

export default chatSocket;