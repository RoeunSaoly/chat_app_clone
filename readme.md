# Real-Time Chat Application

A scalable **real-time chat application** built with an **Android client** and a **Node.js backend**.
The system supports **real-time messaging, authentication, chat rooms, and persistent message storage**.

The architecture is designed for **scalability and maintainability**, using **Redis Pub/Sub** to synchronize messages across multiple backend instances and **Docker** for containerized infrastructure.

---

# Tech Stack

## Mobile (Android)

* **Kotlin**
* **Jetpack Compose**
* **MVVM Architecture**
* **Retrofit** (REST API client)
* **Socket.IO Client** (real-time communication)

## Backend

* **Node.js**
* **Express.js**
* **Socket.IO**

## Infrastructure

* **PostgreSQL** (persistent database)
* **pgAdmin** (database management)
* **Redis** (pub/sub message broker)
* **Docker & Docker Compose**

---

# Architecture Overview

```
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
PostgreSQL Database
```

**Redis Pub/Sub** ensures that messages are synchronized between backend instances when scaling horizontally.

---

# Project Structure

```
realtime-chat-app/
│
├── backend/
│   │
│   ├── src/
│   │   │
│   │   ├── config/
│   │   │   ├── database.js
│   │   │   └── redis.js
│   │   │
│   │   ├── routes/
│   │   │   ├── auth.routes.js
│   │   │   ├── user.routes.js
│   │   │   ├── room.routes.js
│   │   │   └── message.routes.js
│   │   │
│   │   ├── controllers/
│   │   │   ├── auth.controller.js
│   │   │   ├── user.controller.js
│   │   │   ├── room.controller.js
│   │   │   └── message.controller.js
│   │   │
│   │   ├── services/
│   │   │   ├── auth.service.js
│   │   │   ├── user.service.js
│   │   │   ├── room.service.js
│   │   │   └── message.service.js
│   │   │
│   │   ├── models/
│   │   │   ├── user.model.js
│   │   │   ├── room.model.js
│   │   │   └── message.model.js
│   │   │
│   │   ├── sockets/
│   │   │   └── chat.socket.js
│   │   │
│   │   ├── middleware/
│   │   │   ├── auth.middleware.js
│   │   │   └── error.middleware.js
│   │   │
│   │   ├── utils/
│   │   │   ├── jwt.js
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
│       │   │
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
│       │   │
│       │   ├── screens/
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
│       │   ├── AuthViewModel.kt
│       │   └── ChatViewModel.kt
│       │
│       ├── navigation/
│       │   └── NavGraph.kt
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

# Database Schema (PostgreSQL)

## Users Table

```sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Rooms Table

```sql
CREATE TABLE rooms (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Messages Table

```sql
CREATE TABLE messages (
  id SERIAL PRIMARY KEY,
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

```
backend/Dockerfile
```

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

```
docker-compose.yml
```

```yaml
version: "3.9"

services:

  backend:
    build: ./backend
    container_name: chat-backend
    ports:
      - "5000:5000"
    environment:
      DB_HOST: postgres
      DB_USER: chatuser
      DB_PASSWORD: chatpassword
      DB_NAME: chatdb
      REDIS_HOST: redis
    depends_on:
      - postgres
      - redis
    volumes:
      - ./backend:/app
      - /app/node_modules

  postgres:
    image: postgres:15
    container_name: chat-postgres
    restart: always
    environment:
      POSTGRES_DB: chatdb
      POSTGRES_USER: chatuser
      POSTGRES_PASSWORD: chatpassword
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./database/schema.sql:/docker-entrypoint-initdb.d/schema.sql

  pgadmin:
    image: dpage/pgadmin4
    container_name: chat-pgadmin
    restart: always
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@chat.com
      PGADMIN_DEFAULT_PASSWORD: admin123
    ports:
      - "5050:80"
    depends_on:
      - postgres

  redis:
    image: redis:7
    container_name: chat-redis
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

---

# Running the Application

Start all services:

```bash
docker compose up --build
```

Running containers:

* backend
* postgres
* pgadmin
* redis

---

# Access Services

Backend API:

```
http://localhost:5000
```

PostgreSQL:

```
localhost:5432
```

pgAdmin:

```
http://localhost:5050
```

Login credentials:

```
email: admin@chat.com
password: admin123
```

Redis:

```
localhost:6379
```

---

# Android Configuration

When using an **Android Emulator**, the API base URL should be:

```
http://10.0.2.2:5000
```

This maps the emulator network to the host machine.

---

# Real-Time Messaging Flow

1. User sends a message from the Android client.
2. Message is emitted through **Socket.IO**.
3. Backend receives the event.
4. Message is stored in **PostgreSQL**.
5. Message is published via **Redis Pub/Sub**.
6. All backend instances broadcast the message to users in the same room.

---

# Features

* User authentication
* Chat rooms
* Real-time messaging
* Persistent message history
* Redis pub/sub synchronization
* WebSocket communication
* Dockerized infrastructure
* PostgreSQL database
* Android MVVM architecture

---

# Future Improvements

* Media messages (images, files)
* Push notifications
* Typing indicators
* Read receipts
* Online/offline presence
* Message reactions
* End-to-end encryption
* Load balancer support
* Message search

---

# License

MIT License
