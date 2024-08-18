package com.github.brunobaiano;


import com.github.brunobaiano.notification.*;
import com.github.brunobaiano.notification.loader.CsvNotificationLoader;
import com.github.brunobaiano.notification.loader.NotificationLoader;
import com.github.brunobaiano.notification.rate.RateLimitConfigLoader;
import com.github.brunobaiano.notification.rate.RateLimitRule;
import com.github.brunobaiano.notification.rate.RedisRateLimiter;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, RateLimitRule> rateLimitRules = RateLimitConfigLoader.loadRateLimitRules("rate-limits.json");

        NotificationServiceImpl service = new NotificationServiceImpl(new Gateway(),
                new RedisRateLimiter(new Jedis("localhost", 6379)), rateLimitRules);

        NotificationLoader notificationLoader = new CsvNotificationLoader(service);
        notificationLoader.loadNotifications("notifications.csv");

    }
}

