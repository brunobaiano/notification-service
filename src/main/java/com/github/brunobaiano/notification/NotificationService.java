package com.github.brunobaiano.notification;

public interface NotificationService {
    void send(String type, String userId, String message);
}
