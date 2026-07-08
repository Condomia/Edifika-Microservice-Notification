package com.edifika.notification.application.internal.queryservices;

import com.edifika.notification.domain.model.aggregates.DeviceToken;
import com.edifika.notification.domain.model.entities.DeviceTokenEntity;
import com.edifika.notification.domain.services.DeviceTokenQueryService;
import com.edifika.notification.infrastructure.persistence.jpa.repositories.JpaDeviceTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceTokenQueryServiceImpl implements DeviceTokenQueryService {

    private final JpaDeviceTokenRepository jpaDeviceTokenRepository;

    public DeviceTokenQueryServiceImpl(JpaDeviceTokenRepository jpaDeviceTokenRepository) {
        this.jpaDeviceTokenRepository = jpaDeviceTokenRepository;
    }

    @Override
    public Optional<DeviceToken> getTokenByUserId(Long userId) {
        return jpaDeviceTokenRepository.findByUserId(userId)
                .map(this::mapToDomain);
    }

    @Override
    public Optional<DeviceToken> getTokenById(Long id) {
        return jpaDeviceTokenRepository.findById(id)
                .map(this::mapToDomain);
    }

    private DeviceToken mapToDomain(DeviceTokenEntity entity) {
        return new DeviceToken(
                entity.getId(),
                entity.getUserId(),
                entity.getToken(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
