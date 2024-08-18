package com.github.brunobaiano.notification;


import com.github.brunobaiano.notification.rate.RateLimitRule;
import com.github.brunobaiano.notification.rate.RedisRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);


    private Gateway gateway;
    private RedisRateLimiter rateLimiter;

    // Rate limit configuration
    private final Map<String, RateLimitRule> rateLimitRules;

    public NotificationServiceImpl(Gateway gateway, RedisRateLimiter rateLimiter, Map<String, RateLimitRule> rateLimitRules) {
        this.gateway = gateway;
        this.rateLimiter = rateLimiter;
        this.rateLimitRules = Map.copyOf(rateLimitRules);
    }

    @Override
    public void send(String type, String userId, String message) {
        RateLimitRule rule = rateLimitRules.get(type);
        if (rule == null) {
            logger.error("Rate limit rule not found for type {}", type);
            return;
        }

        boolean allowed = rateLimiter.isAllowed(userId, type, rule.limit(), rule.timeToLiveMs());

        if (allowed) {
            gateway.send(userId, message);
        } else {
            logger.warn("Rate limit exceeded for user {} and type {}", userId, type);
        }
    }

}
