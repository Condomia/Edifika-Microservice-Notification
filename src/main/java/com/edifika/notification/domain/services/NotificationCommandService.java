package com.edifika.notification.domain.services;

import com.edifika.notification.domain.model.aggregates.Notification;

public interface NotificationCommandService {
    Notification createNotification(Long userId, String title, String content);

    Notification markAsRead(Long id);
}

