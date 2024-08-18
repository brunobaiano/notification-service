package com.github.brunobaiano.notification.rate;

import redis.clients.jedis.Jedis;

public class RedisRateLimiter {
    private Jedis redis;

    public RedisRateLimiter(Jedis redis) {
        this.redis = redis;
    }



    public boolean isAllowed(String userId, String type, int limit, long ttl) {
        String key = userId + ":" + type;
        // Redis Lua script to atomically check and update the rate limit
        String luaScript = """
                local current = redis.call('GET', KEYS[1]) \
                if current and tonumber(current) >= tonumber(ARGV[1]) then \
                return 0 \
                else \
                redis.call('INCR', KEYS[1]) \
                redis.call('PEXPIRE', KEYS[1], ARGV[2]) \
                return 1 \
                end""";

        Object result = redis.eval(luaScript, 1, key, String.valueOf(limit), String.valueOf(ttl));

        return result.equals(1L);
    }

}
