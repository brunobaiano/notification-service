package com.github.brunobaiano.notification.rate;

public record RateLimitRule(int limit, long timeToLiveMs) {
}
