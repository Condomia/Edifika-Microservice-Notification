package com.edifika.notification.application.internal.commandservices;

import com.edifika.notification.domain.model.aggregates.DeviceToken;
import com.edifika.notification.domain.model.entities.DeviceTokenEntity;
import com.edifika.notification.infrastructure.persistence.jpa.repositories.JpaDeviceTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceTokenCommandServiceImplTest {

    @Mock
    private JpaDeviceTokenRepository jpaDeviceTokenRepository;

    private DeviceTokenCommandServiceImpl deviceTokenCommandService;

    @BeforeEach
    void setUp() {
        deviceTokenCommandService =
                new DeviceTokenCommandServiceImpl(jpaDeviceTokenRepository);
    }

    @Test
    void registerTokenShouldSaveEntityAndReturnMappedDeviceToken() {

        Long userId = 1L;
        String token = "firebase-device-token-123";

        DeviceTokenEntity savedEntity = new DeviceTokenEntity();
        savedEntity.setUserId(userId);
        savedEntity.setToken(token);

        when(jpaDeviceTokenRepository.save(any(DeviceTokenEntity.class)))
                .thenReturn(savedEntity);

        // Act
        DeviceToken result =
                deviceTokenCommandService.registerToken(userId, token);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(token, result.getToken());

        ArgumentCaptor<DeviceTokenEntity> captor =
                ArgumentCaptor.forClass(DeviceTokenEntity.class);

        verify(jpaDeviceTokenRepository).save(captor.capture());

        DeviceTokenEntity capturedEntity = captor.getValue();

        assertEquals(userId, capturedEntity.getUserId());
        assertEquals(token, capturedEntity.getToken());
    }

    @Test
    void registerTokenShouldPropagateExceptionWhenRepositoryFails() {
        // Arrange
        Long userId = 1L;
        String token = "firebase-device-token-123";

        when(jpaDeviceTokenRepository.save(any(DeviceTokenEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deviceTokenCommandService.registerToken(userId, token)
        );

        // Assert
        assertEquals("Database error", exception.getMessage());

        verify(jpaDeviceTokenRepository, times(1))
                .save(any(DeviceTokenEntity.class));
    }
}