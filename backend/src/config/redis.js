import { createClient } from "redis";

const pubClient = createClient({
  url: `redis://${process.env.REDIS_HOST}:6379`
});

const subClient = pubClient.duplicate();

await pubClient.connect();
await subClient.connect();

export { pubClient, subClient };