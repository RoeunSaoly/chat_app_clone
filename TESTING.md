# Testing Guide — Real-Time Chat Feature

## Prerequisites

Ensure PostgreSQL and Redis are running. If using Docker:
```bash
cd /home/rathanak-phan/Desktop/APP/chat_app_clone
docker-compose up -d
```

Create a `.env` file in `backend/`:
```env
PORT=5000
JWT_SECRET=your_jwt_secret_here
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASSWORD=your_db_password
DB_NAME=chat_app
REDIS_HOST=localhost
```

---

## 1. Backend Setup

```bash
cd backend
npm install
npm run migrate        # Create all tables
npm run dev            # Start server on port 5000
```

The server should log:
- `PostgreSQL Connected Successfully`
- `Server running on http://localhost:5000`

---

## 2. REST API Testing (with curl)

### 2.1 Register Users

```bash
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"password123"}'

curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"bob","email":"bob@example.com","password":"password123"}'
```

### 2.2 Login & Save Token

```bash
# Login as Alice
ALICE_TOKEN=$(curl -s -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"password123"}' | jq -r '.token')

echo "Alice Token: $ALICE_TOKEN"

# Login as Bob
BOB_TOKEN=$(curl -s -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"bob@example.com","password":"password123"}' | jq -r '.token')

echo "Bob Token: $BOB_TOKEN"
```

### 2.3 List Users

```bash
curl -X GET http://localhost:5000/api/profile/users \
  -H "Authorization: Bearer $ALICE_TOKEN"
```

### 2.4 Create a Conversation

Find Bob's user ID from the users list, then create a conversation:

```bash
curl -X POST http://localhost:5000/api/chat/conversations \
  -H "Authorization: Bearer $ALICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type":"private","memberIds":[2]}'
```

*(Replace `2` with Bob's actual user ID)*

### 2.5 List Conversations

```bash
curl -X GET http://localhost:5000/api/chat/conversations \
  -H "Authorization: Bearer $ALICE_TOKEN"
```

### 2.6 Send Message via REST

```bash
curl -X POST http://localhost:5000/api/chat/message \
  -H "Authorization: Bearer $ALICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"conversationId":1,"content":"Hello from REST!","messageType":"text"}'
```

### 2.7 Get Messages

```bash
curl -X GET "http://localhost:5000/api/chat/messages/1?limit=20&offset=0" \
  -H "Authorization: Bearer $ALICE_TOKEN"
```

### 2.8 Mark Messages as Seen

```bash
curl -X PATCH http://localhost:5000/api/chat/messages/seen \
  -H "Authorization: Bearer $BOB_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"conversationId":1}'
```

---

## 3. Socket.IO Testing

### Using Socket.IO Client (Node.js)

Install a quick test client:
```bash
npm install -g socket.io-client
```

Create `test-socket.js`:
```javascript
const { io } = require("socket.io-client");

const TOKEN = process.env.TOKEN || "your_jwt_token";
const socket = io("http://localhost:5000", {
  auth: { token: TOKEN }
});

socket.on("connect", () => {
  console.log("Connected:", socket.id);

  // Join a conversation
  socket.emit("join_conversation", "1");

  // Send a message
  socket.emit("send_message", {
    conversation_id: 1,
    content: "Hello via Socket.IO!"
  });

  // Start typing
  socket.emit("typing", { conversation_id: 1 });

  // Stop typing after 2s
  setTimeout(() => {
    socket.emit("stop_typing", { conversation_id: 1 });
  }, 2000);
});

socket.on("new_message", (data) => {
  console.log("New message:", data);
});

socket.on("typing", (data) => {
  console.log("Typing:", data);
});

socket.on("message_seen", (data) => {
  console.log("Message seen:", data);
});

socket.on("user_online", (data) => {
  console.log("User online:", data);
});

socket.on("disconnect", () => {
  console.log("Disconnected");
});

socket.on("error", (err) => {
  console.error("Error:", err);
});
```

Run two instances in separate terminals:

**Terminal 1 — Alice:**
```bash
TOKEN=$ALICE_TOKEN node test-socket.js
```

**Terminal 2 — Bob:**
```bash
TOKEN=$BOB_TOKEN node test-socket.js
```

### Expected Behavior
1. Both clients connect and receive `user_online` events
2. Alice sends a message → Bob receives `new_message`
3. Alice types → Bob receives `typing` event
4. Bob marks message as seen → Alice receives `message_seen`
5. When either client disconnects → other receives `user_offline`

---

## 4. Android Emulator Testing (Full End-to-End)

### 4.1 Network Setup

The Android emulator uses `10.0.2.2` to reach the host computer's `localhost`. The backend is already configured to run on port `5000`.

**Important:** If your backend is binding to `127.0.0.1` only, the emulator cannot reach it. The Express app in `backend/src/app.js` does not explicitly set a host, so Node.js defaults to `0.0.0.0` (all interfaces) — this is correct for emulator access.

**For physical devices:** Replace `10.0.2.2` with your computer's LAN IP address in `RetrofitClient.kt` and `SocketManager.kt`.

### 4.2 Start Backend (on your computer)

```bash
cd /home/rathanak-phan/Desktop/APP/chat_app_clone/backend
npm run dev
```

Verify it is reachable from the emulator host:
```bash
curl http://localhost:5000/
# Expected: {"message":"Server is running"}
```

### 4.3 Open Project in Android Studio

1. Launch **Android Studio**
2. Select **Open** and choose `/home/rathanak-phan/Desktop/APP/chat_app_clone/frontend`
3. Wait for Gradle sync to complete (it will download `lifecycle-viewmodel-compose` and other dependencies)
4. If you see a Gradle sync error about missing dependencies, click **"Sync Now"** in the notification bar

### 4.4 Create Two Emulator Instances

You need **two separate emulator instances** to test real-time messaging between two users.

**Option A: Two different AVDs**
1. Go to **Device Manager** (View → Tool Windows → Device Manager)
2. Create two Pixel emulators with different Android versions (e.g., Pixel 7 API 34 and Pixel 6 API 33)
3. Start both emulators

**Option B: One emulator + one physical device**
1. Start one emulator
2. Enable **USB Debugging** on your physical Android device
3. Connect via USB and authorize the computer
4. Select the physical device from the device dropdown in Android Studio

### 4.5 Build and Install on Both Devices

1. Select the first emulator from the device dropdown
2. Click **Run** (green triangle) or press `Shift + F10`
3. Wait for the app to build and install
4. Select the second emulator/device from the dropdown
5. Click **Run** again

### 4.6 Full Test Flow (Two-User Chat)

Follow this exact sequence on both devices:

#### Device 1 — Register User "Alice"
1. On the **Welcome Screen**, tap **"Sign Up"**
2. Enter:
   - Username: `alice`
   - Email: `alice@example.com`
   - Password: `password123`
3. Tap **Register**
4. You should be taken to the **Home Screen**

#### Device 2 — Register User "Bob"
1. Repeat the same steps with:
   - Username: `bob`
   - Email: `bob@example.com`
   - Password: `password123`

#### Create a Conversation (via REST or DB)
Since the frontend does not yet have a "New Chat" UI, create the conversation via curl:

```bash
# Login as Alice to get token
ALICE_TOKEN=$(curl -s -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"password123"}' | jq -r '.token')

# Get Bob's user ID
curl -X GET http://localhost:5000/api/profile/users -H "Authorization: Bearer $ALICE_TOKEN"

# Create conversation with Bob (replace 2 with Bob's actual ID)
curl -X POST http://localhost:5000/api/chat/conversations \
  -H "Authorization: Bearer $ALICE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type":"private","memberIds":[2]}'
```

#### Test Real-Time Messaging
1. **On both devices:** Pull down on the Home Screen to refresh conversations. You should see the new conversation appear.
2. **Device 1 (Alice):** Tap the conversation with Bob.
3. **Device 2 (Bob):** Tap the conversation with Alice.
4. **Device 1:** Type "Hello Bob!" and tap the **Send** button.
   - Message appears immediately on Alice's screen with a ✓ (sent status)
   - Within 1–2 seconds, Bob's screen shows the message instantly via Socket.IO
5. **Device 2 (Bob):** Type "Hey Alice!" and send.
   - Alice receives it in real time

#### Test Typing Indicators
1. **Device 1 (Alice):** Start typing in the input field (do not send).
2. **Device 2 (Bob):** Watch the top bar — you should see **"Alice is typing..."** appear in green.
3. **Device 1:** Stop typing for 3 seconds.
4. **Device 2:** The typing indicator should disappear.

#### Test Read Receipts
1. **Device 1 (Alice):** Send a message.
2. **Device 2 (Bob):** The message arrives. Keep Bob in the chat screen (messages auto-mark as seen).
3. **Device 1 (Alice):** The ✓ on your sent message should change to **✓✓ (green)** indicating Bob has seen it.

#### Test Online/Offline Status
1. **Device 2 (Bob):** Completely close the app (swipe away from recents).
2. **Device 1 (Alice):** Go back to the Home Screen. After a few seconds, Bob's green **online dot** should disappear.
3. **Device 2 (Bob):** Reopen the app and login again.
4. **Device 1 (Alice):** Bob's online dot should reappear.

#### Test Unread Counts
1. **Device 2 (Bob):** Stay on the Home Screen (do not open the chat).
2. **Device 1 (Alice):** Send multiple messages to Bob.
3. **Device 2 (Bob):** You should see a **red badge** with the unread count on Alice's conversation.
4. **Device 2 (Bob):** Open the chat. The badge disappears and messages are marked as seen.

### 4.7 Viewing Logs

**Android Studio Logcat:**
```
tag:socket.io   # Filter for Socket.IO events
tag:ChatRepo    # Filter for repository logs
tag:ChatVM      # Filter for ViewModel logs
```

**Backend Console:**
Watch the terminal running `npm run dev` for:
- `User connected: <userId>`
- `Message saved:`
- `Emitting typing to conversation:`
- `User disconnected: <userId>`

### 4.8 Troubleshooting Emulator Issues

| Problem | Solution |
|---------|----------|
| `ERR_CONNECTION_REFUSED` | Ensure backend is running and not blocked by firewall. Test with `curl http://10.0.2.2:5000` from the emulator's browser |
| App crashes on login | Check Logcat for JSON parsing errors. Ensure backend returns the expected `AuthResponse` format |
| Messages not sending | Check Logcat for `SocketIO` connection errors. Verify token is saved to SharedPreferences |
| Typing indicator not showing | Ensure both users are in the same conversation room. Check backend logs for `Emitting typing` |
| Read receipts not updating | Verify `mark_all_seen` socket event is emitted when opening chat |
| Green dot not appearing | Check that `user_online` event is received in `HomeViewModel` logs |

---

## 5. Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| `Connection refused` on Android | Ensure backend runs on `0.0.0.0` (default). Use `10.0.2.2:5000` for emulator, your LAN IP for real device |
| Socket.IO auth fails | Check JWT token is saved after login. Verify `auth: { token: ... }` in SocketManager |
| Messages not persisting | Check PostgreSQL is running and migrations applied (`npm run migrate`) |
| Redis connection error | Ensure Redis is running on port 6379 |
| Gradle sync fails | Add `lifecycle-viewmodel-compose` dependency (already added in `build.gradle.kts`) |
| `CORS` error | Already configured as `origin: "*"` in backend |

---

## 6. Quick Health Check

```bash
# Backend health
curl http://localhost:5000/
# Expected: {"message":"Server is running"}

curl http://localhost:5000/api/
# Expected: {"message":"API working"}

# PostgreSQL (from backend logs)
# Expected: "PostgreSQL Connected Successfully"

# Redis
redis-cli ping
# Expected: PONG
```

