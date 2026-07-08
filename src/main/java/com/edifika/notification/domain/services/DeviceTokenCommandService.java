package com.edifika.notification.domain.services;

import com.edifika.notification.domain.model.aggregates.DeviceToken;

public interface DeviceTokenCommandService {
    DeviceToken registerToken(Long userId, String token);
}
