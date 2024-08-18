package com.github.brunobaiano.notification;

import com.github.brunobaiano.notification.rate.RateLimitConfigLoader;
import com.github.brunobaiano.notification.rate.RateLimitRule;
import com.github.brunobaiano.notification.rate.RedisRateLimiter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Testcontainers
public class NotificationServiceImplTest {

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379).withPrivilegedMode(true);

    private static Jedis jedis;
    private Gateway gateway;
    private RedisRateLimiter rateLimiter;
    private NotificationServiceImpl notificationService;
    private Map<String, RateLimitRule> rateLimitRules;

    @BeforeAll
    public static void setUpClass() {
        String address = redisContainer.getHost();
        Integer port = redisContainer.getFirstMappedPort();
        jedis = new Jedis(address, port);

    }

    @BeforeEach
    public void setUp() {
        gateway = mock(Gateway.class);
        rateLimiter = new RedisRateLimiter(jedis);
        rateLimitRules = RateLimitConfigLoader.loadRateLimitRules("rate-limits-test.json");
        notificationService = new NotificationServiceImpl(gateway, rateLimiter, rateLimitRules);
    }

    @Test
    @DisplayName("Test send notification - success")
    public void testSendNotificationAllowed() {
        notificationService.send("status", "user2", "Test message");
        verify(gateway, times(1)).send("user2", "Test message");
    }

    @Test
    @DisplayName("Test send notification - rate limit exceeded")
    public void testSendNotificationRateLimitExceeded() {
        notificationService.send("status", "user1", "Test message");
        notificationService.send("status", "user1", "Test message");
        notificationService.send("status", "user1", "Test message");
        verify(gateway, times(2)).send("user1", "Test message");
    }

    @Test
    @DisplayName("Test send notification - unknown notification type")
    public void testSendUnknownNotificationType() {
        notificationService.send("unknown", "user1", "Test message");
        verify(gateway, times(0)).send("user1", "Test message");
    }
}