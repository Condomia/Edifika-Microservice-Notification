package com.edifika.notification.application.internal.queryservices;

import com.edifika.notification.domain.model.aggregates.DeviceToken;
import com.edifika.notification.domain.model.entities.DeviceTokenEntity;
import com.edifika.notification.infrastructure.persistence.jpa.repositories.JpaDeviceTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceTokenQueryServiceImplTest {

    @Mock
    private JpaDeviceTokenRepository jpaDeviceTokenRepository;

    private DeviceTokenQueryServiceImpl deviceTokenQueryService;

    @BeforeEach
    void setUp() {
        deviceTokenQueryService =
                new DeviceTokenQueryServiceImpl(jpaDeviceTokenRepository);
    }

    @Test
    void getTokenByUserIdShouldReturnDeviceTokenWhenFound() {

        Long userId = 1L;

        DeviceTokenEntity entity = mock(DeviceTokenEntity.class);

        when(entity.getId()).thenReturn(10L);
        when(entity.getUserId()).thenReturn(userId);
        when(entity.getToken()).thenReturn("firebase-token-123");
        when(entity.getCreatedAt()).thenReturn(null);
        when(entity.getUpdatedAt()).thenReturn(null);

        when(jpaDeviceTokenRepository.findByUserId(userId))
                .thenReturn(Optional.of(entity));

        // Act
        Optional<DeviceToken> result =
                deviceTokenQueryService.getTokenByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        assertEquals(userId, result.get().getUserId());
        assertEquals("firebase-token-123", result.get().getToken());

        verify(jpaDeviceTokenRepository, times(1))
                .findByUserId(userId);
    }

    @Test
    void getTokenByUserIdShouldReturnEmptyWhenNotFound() {
        // Arrange
        Long userId = 99L;

        when(jpaDeviceTokenRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        // Act
        Optional<DeviceToken> result =
                deviceTokenQueryService.getTokenByUserId(userId);

        // Assert
        assertTrue(result.isEmpty());

        verify(jpaDeviceTokenRepository, times(1))
                .findByUserId(userId);
    }

    @Test
    void getTokenByIdShouldReturnDeviceTokenWhenFound() {
        // Arrange
        Long id = 10L;

        DeviceTokenEntity entity = mock(DeviceTokenEntity.class);

        when(entity.getId()).thenReturn(id);
        when(entity.getUserId()).thenReturn(1L);
        when(entity.getToken()).thenReturn("firebase-token-456");
        when(entity.getCreatedAt()).thenReturn(null);
        when(entity.getUpdatedAt()).thenReturn(null);

        when(jpaDeviceTokenRepository.findById(id))
                .thenReturn(Optional.of(entity));

        // Act
        Optional<DeviceToken> result =
                deviceTokenQueryService.getTokenById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals(1L, result.get().getUserId());
        assertEquals("firebase-token-456", result.get().getToken());

        verify(jpaDeviceTokenRepository, times(1))
                .findById(id);
    }

    @Test
    void getTokenByIdShouldReturnEmptyWhenNotFound() {
        // Arrange
        Long id = 99L;

        when(jpaDeviceTokenRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act
        Optional<DeviceToken> result =
                deviceTokenQueryService.getTokenById(id);

        // Assert
        assertTrue(result.isEmpty());

        verify(jpaDeviceTokenRepository, times(1))
                .findById(id);
    }
}