import { createClient } from "redis";

let redisUrl = process.env.REDIS_URL || "";

// If no REDIS_URL, build one from host/port/password
if (!redisUrl && process.env.REDIS_HOST) {
  redisUrl = process.env.REDIS_PASSWORD
    ? `redis://:${process.env.REDIS_PASSWORD}@${process.env.REDIS_HOST}:${process.env.REDIS_PORT || 6379}`
    : `redis://${process.env.REDIS_HOST}:${process.env.REDIS_PORT || 6379}`;
}

if (redisUrl) {
  // 1. Strip all leading scheme prefixes (handles "redis://redis://..." double-prefix)
  redisUrl = redisUrl.replace(/^(redis:\/\/|rediss:\/\/)+/, "");

  // 2. Re-add exactly one correct scheme prefix
  redisUrl = `redis://${redisUrl}`;

  // 3. Fix duplicate ports (e.g. "host:6379:6379" → "host:6379")
  redisUrl = redisUrl.replace(/:(\d+):\1$/, ":$1");

  // 4. Remove trailing slashes
  redisUrl = redisUrl.replace(/\/$/, "");

  const maskedUrl = redisUrl.replace(/:[^:@]+@/, ":****@");
  console.log(`Connecting to Redis at: ${maskedUrl}`);
} else {
  console.warn(
    "No Redis configuration found (REDIS_URL or REDIS_HOST). Redis features may not work."
  );
}

const pubClient = createClient(redisUrl ? { url: redisUrl } : {});
pubClient.on("error", (err) => console.error("Redis Pub Client Error:", err));

const subClient = pubClient.duplicate();
subClient.on("error", (err) => console.error("Redis Sub Client Error:", err));

try {
  if (redisUrl) {
    await pubClient.connect();
    await subClient.connect();
    console.log("Redis connected successfully");
  }
} catch (error) {
  console.error("Failed to connect to Redis:", error.message);
  // Continue anyway — socket.io will fall back gracefully
}

export { pubClient, subClient };