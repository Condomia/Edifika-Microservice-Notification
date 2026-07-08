package com.edifika.notification.interfaces.rest.resources;

import com.edifika.notification.domain.model.valueobjects.NotificationStatus;

import java.util.Date;

public record NotificationResource(
        Long id,
        Long userId,
        String title,
        String content,
        NotificationStatus status,
        Date createdAt
) {
}
