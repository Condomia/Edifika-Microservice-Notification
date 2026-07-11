package com.edifika.notification.interfaces.rest;

import com.edifika.notification.domain.model.aggregates.DeviceToken;
import com.edifika.notification.domain.services.DeviceTokenCommandService;
import com.edifika.notification.domain.services.DeviceTokenQueryService;
import com.edifika.notification.interfaces.rest.resources.CreateDeviceTokenResource;
import com.edifika.notification.interfaces.rest.resources.DeviceTokenResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceTokenControllerTest {

    @Mock
    private DeviceTokenCommandService deviceTokenCommandService;

    @Mock
    private DeviceTokenQueryService deviceTokenQueryService;

    private DeviceTokenController deviceTokenController;

    @BeforeEach
    void setUp() {
        deviceTokenController = new DeviceTokenController(
                deviceTokenCommandService,
                deviceTokenQueryService
        );
    }

    @Test
    void registerTokenShouldReturnCreatedDeviceToken() {
        // Arrange
        Long userId = 1L;
        String token = "firebase-token-123";

        CreateDeviceTokenResource resource =
                new CreateDeviceTokenResource(userId, token);

        DeviceToken deviceToken = mock(DeviceToken.class);

        when(deviceToken.getId()).thenReturn(10L);
        when(deviceToken.getUserId()).thenReturn(userId);
        when(deviceToken.getToken()).thenReturn(token);
        when(deviceToken.getCreatedAt()).thenReturn(null);
        when(deviceToken.getUpdatedAt()).thenReturn(null);

        when(deviceTokenCommandService.registerToken(userId, token))
                .thenReturn(deviceToken);

        // Act
        ResponseEntity<DeviceTokenResource> response =
                deviceTokenController.registerToken(resource);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(10L, response.getBody().id());
        assertEquals(userId, response.getBody().userId());
        assertEquals(token, response.getBody().token());

        verify(deviceTokenCommandService, times(1))
                .registerToken(userId, token);

        verifyNoInteractions(deviceTokenQueryService);
    }

    @Test
    void getTokenByUserIdShouldReturnOkWhenTokenExists() {
        // Arrange
        Long userId = 1L;

        DeviceToken deviceToken = mock(DeviceToken.class);

        when(deviceToken.getId()).thenReturn(10L);
        when(deviceToken.getUserId()).thenReturn(userId);
        when(deviceToken.getToken()).thenReturn("firebase-token-123");
        when(deviceToken.getCreatedAt()).thenReturn(null);
        when(deviceToken.getUpdatedAt()).thenReturn(null);

        when(deviceTokenQueryService.getTokenByUserId(userId))
                .thenReturn(Optional.of(deviceToken));

        // Act
        ResponseEntity<DeviceTokenResource> response =
                deviceTokenController.getTokenByUserId(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(10L, response.getBody().id());
        assertEquals(userId, response.getBody().userId());
        assertEquals("firebase-token-123", response.getBody().token());

        verify(deviceTokenQueryService, times(1))
                .getTokenByUserId(userId);

        verifyNoInteractions(deviceTokenCommandService);
    }

    @Test
    void getTokenByUserIdShouldReturnNotFoundWhenTokenDoesNotExist() {
        // Arrange
        Long userId = 99L;

        when(deviceTokenQueryService.getTokenByUserId(userId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<DeviceTokenResource> response =
                deviceTokenController.getTokenByUserId(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(deviceTokenQueryService, times(1))
                .getTokenByUserId(userId);

        verifyNoInteractions(deviceTokenCommandService);
    }

    @Test
    void getTokenByIdShouldReturnOkWhenTokenExists() {
        // Arrange
        Long id = 10L;
        Long userId = 1L;

        DeviceToken deviceToken = mock(DeviceToken.class);

        when(deviceToken.getId()).thenReturn(id);
        when(deviceToken.getUserId()).thenReturn(userId);
        when(deviceToken.getToken()).thenReturn("firebase-token-456");
        when(deviceToken.getCreatedAt()).thenReturn(null);
        when(deviceToken.getUpdatedAt()).thenReturn(null);

        when(deviceTokenQueryService.getTokenById(id))
                .thenReturn(Optional.of(deviceToken));

        // Act
        ResponseEntity<DeviceTokenResource> response =
                deviceTokenController.getTokenById(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(id, response.getBody().id());
        assertEquals(userId, response.getBody().userId());
        assertEquals("firebase-token-456", response.getBody().token());

        verify(deviceTokenQueryService, times(1))
                .getTokenById(id);

        verifyNoInteractions(deviceTokenCommandService);
    }

    @Test
    void getTokenByIdShouldReturnNotFoundWhenTokenDoesNotExist() {
        // Arrange
        Long id = 99L;

        when(deviceTokenQueryService.getTokenById(id))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<DeviceTokenResource> response =
                deviceTokenController.getTokenById(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(deviceTokenQueryService, times(1))
                .getTokenById(id);

        verifyNoInteractions(deviceTokenCommandService);
    }
}