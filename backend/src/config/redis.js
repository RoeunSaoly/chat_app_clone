import { createClient } from "redis";

let redisUrl = process.env.REDIS_URL;

if (!redisUrl) {
  redisUrl = process.env.REDIS_PASSWORD 
    ? `redis://:${process.env.REDIS_PASSWORD}@${process.env.REDIS_HOST}:${process.env.REDIS_PORT || 6379}`
    : `redis://${process.env.REDIS_HOST}:${process.env.REDIS_PORT || 6379}`;
}

// Ensure the URL has the redis:// prefix
if (redisUrl && !redisUrl.startsWith('redis://') && !redisUrl.startsWith('rediss://')) {
  redisUrl = `redis://${redisUrl}`;
}

const pubClient = createClient({
  url: redisUrl
});

pubClient.on('error', (err) => console.error('Redis Pub Client Error:', err));

const subClient = pubClient.duplicate();
subClient.on('error', (err) => console.error('Redis Sub Client Error:', err));

// Attempt to connect, but don't let it crash the whole process if it fails initially
// Note: In production, you might want a more robust retry strategy
try {
    await pubClient.connect();
    await subClient.connect();
    console.log("Redis connected successfully");
} catch (error) {
    console.error("Failed to connect to Redis:", error.message);
    // Continue anyway, socket.io will just fall back to local if adapter fails
    // or keep retrying depending on library behavior
}

export { pubClient, subClient };