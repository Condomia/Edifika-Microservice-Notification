package com.edifika.notification.infrastructure.persistence.jpa.repositories;

import com.edifika.notification.domain.model.entities.DeviceTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaDeviceTokenRepository extends JpaRepository<DeviceTokenEntity, Long> {
    Optional<DeviceTokenEntity> findByUserId(Long userId);
}
