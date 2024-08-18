package com.github.brunobaiano.notification.loader;

import com.github.brunobaiano.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CsvNotificationLoader implements NotificationLoader {

    private NotificationService service;

    public CsvNotificationLoader(NotificationService notificationService) {
        this.service = notificationService;
    }


    public void loadNotifications(String filePath) {
        Logger logger = LoggerFactory.getLogger(CsvNotificationLoader.class);

        record Notification(String type, String userId, String message) {}

        try (InputStream inputStream = CsvNotificationLoader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + filePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                reader.lines().forEach(line -> {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        Notification notification = new Notification(parts[0].trim(), parts[1].trim(), parts[2].trim());
                        service.send(notification.type(), notification.userId(), notification.message());
                    }
                    else{
                        throw new RuntimeException("Invalid notification format: " + line);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load notifications from CSV", e);
        }
    }
}