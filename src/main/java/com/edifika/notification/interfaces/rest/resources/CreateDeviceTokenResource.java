package com.edifika.notification.interfaces.rest.resources;

public record CreateDeviceTokenResource(
        Long userId,
        String token
) {
}
