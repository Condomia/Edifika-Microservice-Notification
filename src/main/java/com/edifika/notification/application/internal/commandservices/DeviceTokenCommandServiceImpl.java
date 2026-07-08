package com.edifika.notification.application.internal.commandservices;

import com.edifika.notification.domain.model.aggregates.DeviceToken;
import com.edifika.notification.domain.model.entities.DeviceTokenEntity;
import com.edifika.notification.domain.services.DeviceTokenCommandService;
import com.edifika.notification.infrastructure.persistence.jpa.repositories.JpaDeviceTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceTokenCommandServiceImpl implements DeviceTokenCommandService {

    private final JpaDeviceTokenRepository jpaDeviceTokenRepository;

    public DeviceTokenCommandServiceImpl(JpaDeviceTokenRepository jpaDeviceTokenRepository) {
        this.jpaDeviceTokenRepository = jpaDeviceTokenRepository;
    }

    @Override
    public DeviceToken registerToken(Long userId, String token) {
        DeviceTokenEntity entity = new DeviceTokenEntity();
        entity.setUserId(userId);
        entity.setToken(token);

        DeviceTokenEntity savedEntity = jpaDeviceTokenRepository.save(entity);
        return mapToDomain(savedEntity);
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
