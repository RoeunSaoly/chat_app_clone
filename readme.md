# Real-Time Chat Application

A scalable **real-time chat application** built with an Android client and a Node.js backend.
The system supports real-time messaging, authentication, chat rooms, and persistent message storage.

The architecture is designed for **scalability**, using Redis pub/sub to synchronize messages across multiple backend instances.

---

# Tech Stack

## Mobile (Android)

* Kotlin
* Jetpack Compose
* Retrofit (REST API client)
* Socket.IO client (WebSocket communication)

## Backend

* Node.js
* Express.js
* Socket.IO

## Infrastructure

* Redis (pub/sub message broker)
* MySQL (persistent database)
* Docker & Docker Compose

---

# Architecture Overview

```text
Android Client
      │
      │ REST API (Authentication, Users, Rooms)
      ▼
Express Backend
      │
      │ WebSocket
      ▼
Socket.IO
      │
      ▼
Redis Pub/Sub
      │
      ▼
Multiple Backend Instances
      │
      ▼
MySQL Database
```

Redis ensures that messages are synchronized between backend instances when scaling horizontally.

---

# Project Structure

```text
realtime-chat-app/
│
├── backend/
│   │
│   ├── src/
│   │   │
│   │   ├── config/
│   │   │   ├── db.js
│   │   │   ├── redis.js
│   │   │   └── socket.js
│   │   │
│   │   ├── controllers/
│   │   │   ├── authController.js
│   │   │   └── chatController.js
│   │   │
│   │   ├── services/
│   │   │   ├── authService.js
│   │   │   └── chatService.js
│   │   │
│   │   ├── models/
│   │   │   ├── userModel.js
│   │   │   ├── roomModel.js
│   │   │   └── messageModel.js
│   │   │
│   │   ├── routes/
│   │   │   ├── authRoutes.js
│   │   │   └── chatRoutes.js
│   │   │
│   │   ├── sockets/
│   │   │   └── chatSocket.js
│   │   │
│   │   ├── middleware/
│   │   │   └── authMiddleware.js
│   │   │
│   │   ├── utils/
│   │   │   └── logger.js
│   │   │
│   │   └── app.js
│   │
│   ├── server.js
│   ├── Dockerfile
│   ├── package.json
│   └── .env
│
├── mobile/
│   │
│   └── app/src/main/java/com/chatapp/
│       │
│       ├── data/
│       │   ├── api/
│       │   │   ├── ApiService.kt
│       │   │   └── SocketClient.kt
│       │   │
│       │   ├── model/
│       │   │   ├── User.kt
│       │   │   ├── Message.kt
│       │   │   └── ChatRoom.kt
│       │   │
│       │   └── repository/
│       │       └── ChatRepository.kt
│       │
│       ├── ui/
│       │   ├── screen/
│       │   │   ├── LoginScreen.kt
│       │   │   ├── ChatListScreen.kt
│       │   │   └── ChatRoomScreen.kt
│       │   │
│       │   ├── components/
│       │   │   ├── ChatBubble.kt
│       │   │   └── MessageInput.kt
│       │   │
│       │   └── theme/
│       │       └── Theme.kt
│       │
│       ├── viewmodel/
│       │   └── ChatViewModel.kt
│       │
│       └── MainActivity.kt
│
├── database/
│   └── schema.sql
│
├── docker/
│   └── redis/
│       └── redis.conf
│
├── docker-compose.yml
└── README.md
```

---

# Database Schema

## users

```sql
CREATE TABLE users (
 id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100),
 email VARCHAR(255) UNIQUE,
 password VARCHAR(255),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## rooms

```sql
CREATE TABLE rooms (
 id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## messages

```sql
CREATE TABLE messages (
 id INT AUTO_INCREMENT PRIMARY KEY,
 room_id INT,
 sender_id INT,
 message TEXT,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (room_id) REFERENCES rooms(id),
 FOREIGN KEY (sender_id) REFERENCES users(id)
);
```

## full schema

```sql
CREATE TABLE users (
 id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100),
 email VARCHAR(255) UNIQUE,
 password VARCHAR(255),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
 id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100),
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE messages (
 id INT AUTO_INCREMENT PRIMARY KEY,
 room_id INT,
 sender_id INT,
 message TEXT,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (room_id) REFERENCES rooms(id),
 FOREIGN KEY (sender_id) REFERENCES users(id)
);
```

---

# Docker Setup

## Backend Dockerfile

backend/Dockerfile

```dockerfile
FROM node:20-alpine

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

EXPOSE 5000

CMD ["npm","run","dev"]
```

---

# Docker Compose

docker-compose.yml

```yaml
version: "3.9"

services:

  backend:
    build: ./backend
    container_name: chat-backend
    ports:
      - "5000:5000"
    environment:
      DB_HOST: mysql
      DB_USER: chatuser
      DB_PASSWORD: chatpassword
      DB_NAME: chatdb
      REDIS_HOST: redis
    depends_on:
      - mysql
      - redis
    volumes:
      - ./backend:/app
      - /app/node_modules

  mysql:
    image: mysql:8
    container_name: chat-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: chatdb
      MYSQL_USER: chatuser
      MYSQL_PASSWORD: chatpassword
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/schema.sql:/docker-entrypoint-initdb.d/schema.sql

  redis:
    image: redis:7
    container_name: chat-redis
    ports:
      - "6379:6379"

volumes:
  mysql_data:
```

---

# Running the Application

Start all services:

```bash
docker compose up --build
```

Running containers:

* backend
* mysql
* redis

Backend API:

```
http://localhost:5000
```

MySQL:

```
localhost:3306
```

Redis:

```
localhost:6379
```

---

# Android Configuration

For Android emulator API calls use:

```
http://10.0.2.2:5000
```

This maps the emulator network to the host machine.

---

# Real-Time Messaging Flow

1. User sends message from Android client.
2. Message is emitted through Socket.IO.
3. Backend receives event.
4. Message is saved to MySQL.
5. Event is published via Redis pub/sub.
6. All connected servers broadcast message to users in the same room.

---

# Features

* User authentication
* Chat rooms
* Real-time messaging
* Persistent message history
* Redis pub/sub synchronization
* WebSocket communication
* Scalable backend architecture
* Dockerized services

---

# Future Improvements

* Media messages (images, files)
* Push notifications
* Typing indicators
* Read receipts
* Online/offline presence
* Message reactions
* Message encryption
* Load balancer support

---

# License

MIT License
