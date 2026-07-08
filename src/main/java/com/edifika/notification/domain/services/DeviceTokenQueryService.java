package com.edifika.notification.domain.services;

import com.edifika.notification.domain.model.aggregates.DeviceToken;

import java.util.Optional;

public interface DeviceTokenQueryService {
    Optional<DeviceToken> getTokenByUserId(Long userId);
    Optional<DeviceToken> getTokenById(Long id);
}
