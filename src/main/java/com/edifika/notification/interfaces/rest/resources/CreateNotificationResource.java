package com.edifika.notification.interfaces.rest.resources;

public record CreateNotificationResource(
        Long userId,
        String title,
        String content
) {
}
