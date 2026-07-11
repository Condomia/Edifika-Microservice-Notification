package com.edifika.notification.application.internal.commandservices;

import com.edifika.notification.domain.exception.NotificationNotFoundException;
import com.edifika.notification.domain.model.aggregates.DeviceToken;
import com.edifika.notification.domain.model.aggregates.Notification;
import com.edifika.notification.domain.model.entities.NotificationEntity;
import com.edifika.notification.domain.model.valueobjects.NotificationStatus;
import com.edifika.notification.domain.services.DeviceTokenQueryService;
import com.edifika.notification.domain.services.FirebaseNotificationGateway;
import com.edifika.notification.infrastructure.persistence.jpa.repositories.JpaNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceImplTest {

    @Mock
    private JpaNotificationRepository jpaNotificationRepository;

    @Mock
    private DeviceTokenQueryService deviceTokenQueryService;

    @Mock
    private FirebaseNotificationGateway firebaseNotificationGateway;

    private NotificationCommandServiceImpl notificationCommandService;

    @BeforeEach
    void setUp() {
        notificationCommandService = new NotificationCommandServiceImpl(
                jpaNotificationRepository,
                deviceTokenQueryService,
                firebaseNotificationGateway
        );
    }

    @Test
    void createNotificationShouldSaveNotificationAndSendPushWhenTokenExists() {

        Long userId = 1L;
        String title = "Payment reminder";
        String content = "You have a pending payment";
        String token = "firebase-token-123";

        NotificationEntity savedEntity = mock(NotificationEntity.class);
        DeviceToken deviceToken = mock(DeviceToken.class);

        when(savedEntity.getId()).thenReturn(10L);
        when(savedEntity.getUserId()).thenReturn(userId);
        when(savedEntity.getTitle()).thenReturn(title);
        when(savedEntity.getContent()).thenReturn(content);
        when(savedEntity.getStatus()).thenReturn(NotificationStatus.UNREAD);
        when(savedEntity.getCreatedAt()).thenReturn(null);

        when(deviceToken.getToken()).thenReturn(token);

        when(jpaNotificationRepository.save(any(NotificationEntity.class)))
                .thenReturn(savedEntity);

        when(deviceTokenQueryService.getTokenByUserId(userId))
                .thenReturn(Optional.of(deviceToken));

        // Act
        Notification result = notificationCommandService.createNotification(
                userId,
                title,
                content
        );

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
        assertEquals(NotificationStatus.UNREAD, result.getStatus());

        ArgumentCaptor<NotificationEntity> captor =
                ArgumentCaptor.forClass(NotificationEntity.class);

        verify(jpaNotificationRepository).save(captor.capture());

        NotificationEntity capturedEntity = captor.getValue();

        assertEquals(userId, capturedEntity.getUserId());
        assertEquals(title, capturedEntity.getTitle());
        assertEquals(content, capturedEntity.getContent());
        assertEquals(NotificationStatus.UNREAD, capturedEntity.getStatus());

        verify(deviceTokenQueryService).getTokenByUserId(userId);

        verify(firebaseNotificationGateway).sendPushNotification(
                token,
                title,
                content
        );
    }

    @Test
    void createNotificationShouldNotSendPushWhenTokenDoesNotExist() {
        // Arrange
        Long userId = 1L;
        String title = "New announcement";
        String content = "There is a new condominium announcement";

        NotificationEntity savedEntity = mock(NotificationEntity.class);

        when(savedEntity.getId()).thenReturn(10L);
        when(savedEntity.getUserId()).thenReturn(userId);
        when(savedEntity.getTitle()).thenReturn(title);
        when(savedEntity.getContent()).thenReturn(content);
        when(savedEntity.getStatus()).thenReturn(NotificationStatus.UNREAD);
        when(savedEntity.getCreatedAt()).thenReturn(null);

        when(jpaNotificationRepository.save(any(NotificationEntity.class)))
                .thenReturn(savedEntity);

        when(deviceTokenQueryService.getTokenByUserId(userId))
                .thenReturn(Optional.empty());

        // Act
        Notification result = notificationCommandService.createNotification(
                userId,
                title,
                content
        );

        // Assert
        assertNotNull(result);
        assertEquals(NotificationStatus.UNREAD, result.getStatus());

        verify(jpaNotificationRepository, times(1))
                .save(any(NotificationEntity.class));

        verify(deviceTokenQueryService, times(1))
                .getTokenByUserId(userId);

        verifyNoInteractions(firebaseNotificationGateway);
    }

    @Test
    void markAsReadShouldUpdateStatusAndReturnNotification() {
        // Arrange
        Long notificationId = 10L;

        NotificationEntity existingEntity = mock(NotificationEntity.class);
        NotificationEntity updatedEntity = mock(NotificationEntity.class);

        when(jpaNotificationRepository.findById(notificationId))
                .thenReturn(Optional.of(existingEntity));

        when(jpaNotificationRepository.save(existingEntity))
                .thenReturn(updatedEntity);

        when(updatedEntity.getId()).thenReturn(notificationId);
        when(updatedEntity.getUserId()).thenReturn(1L);
        when(updatedEntity.getTitle()).thenReturn("Payment reminder");
        when(updatedEntity.getContent()).thenReturn("You have a pending payment");
        when(updatedEntity.getStatus()).thenReturn(NotificationStatus.READ);
        when(updatedEntity.getCreatedAt()).thenReturn(null);

        // Act
        Notification result =
                notificationCommandService.markAsRead(notificationId);

        // Assert
        assertNotNull(result);
        assertEquals(notificationId, result.getId());
        assertEquals(NotificationStatus.READ, result.getStatus());

        verify(existingEntity).setStatus(NotificationStatus.READ);

        verify(jpaNotificationRepository).findById(notificationId);
        verify(jpaNotificationRepository).save(existingEntity);

        verifyNoInteractions(
                deviceTokenQueryService,
                firebaseNotificationGateway
        );
    }

    @Test
    void markAsReadShouldThrowExceptionWhenNotificationDoesNotExist() {
        // Arrange
        Long notificationId = 99L;

        when(jpaNotificationRepository.findById(notificationId))
                .thenReturn(Optional.empty());

        // Act
        NotificationNotFoundException exception = assertThrows(
                NotificationNotFoundException.class,
                () -> notificationCommandService.markAsRead(notificationId)
        );

        // Assert
        assertEquals(
                "Notification with id 99 not found",
                exception.getMessage()
        );

        verify(jpaNotificationRepository).findById(notificationId);
        verify(jpaNotificationRepository, never())
                .save(any(NotificationEntity.class));

        verifyNoInteractions(
                deviceTokenQueryService,
                firebaseNotificationGateway
        );
    }
}