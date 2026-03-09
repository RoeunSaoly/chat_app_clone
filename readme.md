# Real-Time Chat App

**Kotlin + Jetpack Compose + Express.js + MySQL**

A full-stack **real-time chat application** consisting of an Android mobile client and a Node.js backend.
The system supports authentication, real-time messaging, chat rooms, and message history.

---

# Tech Stack

## Mobile (Android)

* Kotlin
* Jetpack Compose
* Retrofit (REST API)
* WebSocket Client (Socket.IO)

## Backend

* Node.js
* Express.js
* Socket.IO
* JWT Authentication

## Database

* MySQL

---

# Project Structure

```
realtime-chat-app/
│
├── backend/              # Express + Socket.IO backend
├── mobile/               # Kotlin Android app
├── database/             # MySQL schema
├── docs/                 # API documentation
├── docker/               # Docker configuration (optional)
└── README.md
```

---

# Backend Structure

```
backend/
│
├── src/
│   │
│   ├── config/
│   │   ├── db.js
│   │   └── socket.js
│   │
│   ├── controllers/
│   │   ├── authController.js
│   │   └── chatController.js
│   │
│   ├── models/
│   │   ├── userModel.js
│   │   ├── messageModel.js
│   │   └── roomModel.js
│   │
│   ├── routes/
│   │   ├── authRoutes.js
│   │   └── chatRoutes.js
│   │
│   ├── services/
│   │   ├── authService.js
│   │   └── chatService.js
│   │
│   ├── sockets/
│   │   └── chatSocket.js
│   │
│   ├── middleware/
│   │   └── authMiddleware.js
│   │
│   ├── utils/
│   │   └── logger.js
│   │
│   └── app.js
│
├── server.js
├── package.json
└── .env
```

---

# Android Structure

```
mobile/
│
└── app/src/main/java/com/chatapp/
│
├── data/
│   ├── api/
│   │   ├── ApiService.kt
│   │   └── SocketClient.kt
│   │
│   ├── model/
│   │   ├── User.kt
│   │   ├── Message.kt
│   │   └── ChatRoom.kt
│   │
│   └── repository/
│       └── ChatRepository.kt
│
├── ui/
│   ├── screen/
│   │   ├── LoginScreen.kt
│   │   ├── ChatListScreen.kt
│   │   └── ChatRoomScreen.kt
│   │
│   ├── components/
│   │   ├── ChatBubble.kt
│   │   └── MessageInput.kt
│   │
│   └── theme/
│       └── Theme.kt
│
├── viewmodel/
│   └── ChatViewModel.kt
│
└── MainActivity.kt
```

---

# Database Schema

## users

```
id INT PRIMARY KEY AUTO_INCREMENT
name VARCHAR(100)
email VARCHAR(255)
password VARCHAR(255)
created_at TIMESTAMP
```

## rooms

```
id INT PRIMARY KEY AUTO_INCREMENT
name VARCHAR(100)
created_at TIMESTAMP
```

## messages

```
id INT PRIMARY KEY AUTO_INCREMENT
room_id INT
sender_id INT
message TEXT
created_at TIMESTAMP
```

---

# Real-Time Communication Flow

```
Android App
     │
     │ REST API (Login/Register)
     ▼
Express Server
     │
     │ WebSocket
     ▼
Socket.IO
     │
     ▼
Other Users
```

Message flow:

```
User A → Socket.IO → Express → MySQL → Socket.IO → User B
```

---

# Features

* User authentication (JWT)
* Real-time messaging
* Chat rooms
* Message history
* Online / offline users
* Typing indicator
* Read receipts
* Push notifications (optional)

---

# Installation

## Backend

```
cd backend
npm install
npm run dev
```

## Android

Open the `mobile` folder using **Android Studio** and run the project.

---

# Future Improvements

* Media messages (images, files)
* Voice messages
* Video calling
* End-to-end encryption
* Message reactions
* Dark mode UI

---

# License

MIT License
