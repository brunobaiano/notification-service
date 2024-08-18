package com.github.brunobaiano.notification.rate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RateLimitConfigLoader {

    public static Map<String, RateLimitRule> loadRateLimitRules(String filePath) {
        Map<String, RateLimitRule> rateLimitRules = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = RateLimitConfigLoader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + filePath);
            }
            JsonNode root = mapper.readTree(inputStream);
            root.fields().forEachRemaining(
                    entry -> {
                        String type = entry.getKey();
                        JsonNode rule = entry.getValue();
                        int limit = rule.get("limit").asInt();
                        long interval = rule.get("interval").asLong();
                        rateLimitRules.put(type, new RateLimitRule(limit, interval));
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rateLimitRules;
    }
}
