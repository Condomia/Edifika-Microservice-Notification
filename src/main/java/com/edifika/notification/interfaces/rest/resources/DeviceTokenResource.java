package com.edifika.notification.interfaces.rest.resources;

import java.util.Date;

public record DeviceTokenResource(
        Long id,
        Long userId,
        String token,
        Date createdAt,
        Date updatedAt
) {
}
