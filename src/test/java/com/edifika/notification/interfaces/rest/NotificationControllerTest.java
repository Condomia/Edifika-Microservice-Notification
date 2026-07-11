package com.edifika.notification.interfaces.rest;

import com.edifika.notification.domain.model.aggregates.Notification;
import com.edifika.notification.domain.model.valueobjects.NotificationStatus;
import com.edifika.notification.domain.services.NotificationCommandService;
import com.edifika.notification.domain.services.NotificationQueryService;
import com.edifika.notification.interfaces.rest.resources.CreateNotificationResource;
import com.edifika.notification.interfaces.rest.resources.NotificationResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationCommandService notificationCommandService;

    @Mock
    private NotificationQueryService notificationQueryService;

    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        notificationController = new NotificationController(
                notificationCommandService,
                notificationQueryService
        );
    }

    @Test
    void createNotificationShouldReturnCreatedNotification() {
        // Arrange
        Long userId = 1L;
        String title = "Payment reminder";
        String content = "You have a pending payment";

        CreateNotificationResource resource =
                new CreateNotificationResource(userId, title, content);

        Notification notification = mock(Notification.class);

        when(notification.getId()).thenReturn(10L);
        when(notification.getUserId()).thenReturn(userId);
        when(notification.getTitle()).thenReturn(title);
        when(notification.getContent()).thenReturn(content);
        when(notification.getStatus()).thenReturn(NotificationStatus.UNREAD);
        when(notification.getCreatedAt()).thenReturn(null);

        when(notificationCommandService.createNotification(
                userId,
                title,
                content
        )).thenReturn(notification);

        // Act
        ResponseEntity<NotificationResource> response =
                notificationController.createNotification(resource);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        NotificationResource body = response.getBody();

        assertEquals(10L, body.id());
        assertEquals(userId, body.userId());
        assertEquals(title, body.title());
        assertEquals(content, body.content());
        assertEquals(NotificationStatus.UNREAD, body.status());

        verify(notificationCommandService, times(1))
                .createNotification(userId, title, content);

        verifyNoInteractions(notificationQueryService);
    }

    @Test
    void getNotificationsByUserShouldReturnOkWithPagedNotifications() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Notification firstNotification = mock(Notification.class);
        Notification secondNotification = mock(Notification.class);

        when(firstNotification.getId()).thenReturn(10L);
        when(firstNotification.getUserId()).thenReturn(userId);
        when(firstNotification.getTitle()).thenReturn("Payment reminder");
        when(firstNotification.getContent())
                .thenReturn("You have a pending payment");
        when(firstNotification.getStatus())
                .thenReturn(NotificationStatus.UNREAD);
        when(firstNotification.getCreatedAt()).thenReturn(null);

        when(secondNotification.getId()).thenReturn(11L);
        when(secondNotification.getUserId()).thenReturn(userId);
        when(secondNotification.getTitle()).thenReturn("Reservation approved");
        when(secondNotification.getContent())
                .thenReturn("Your reservation was approved");
        when(secondNotification.getStatus())
                .thenReturn(NotificationStatus.READ);
        when(secondNotification.getCreatedAt()).thenReturn(null);

        Page<Notification> notificationPage = new PageImpl<>(
                List.of(firstNotification, secondNotification),
                pageable,
                2
        );

        when(notificationQueryService.getNotificationsByUser(
                userId,
                pageable
        )).thenReturn(notificationPage);

        // Act
        ResponseEntity<Page<NotificationResource>> response =
                notificationController.getNotificationsByUser(
                        userId,
                        pageable
                );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Page<NotificationResource> body = response.getBody();

        assertEquals(2, body.getTotalElements());
        assertEquals(2, body.getContent().size());
        assertEquals(1, body.getTotalPages());
        assertEquals(0, body.getNumber());
        assertEquals(10, body.getSize());

        NotificationResource firstResource = body.getContent().get(0);

        assertEquals(10L, firstResource.id());
        assertEquals(userId, firstResource.userId());
        assertEquals("Payment reminder", firstResource.title());
        assertEquals(NotificationStatus.UNREAD, firstResource.status());

        NotificationResource secondResource = body.getContent().get(1);

        assertEquals(11L, secondResource.id());
        assertEquals(userId, secondResource.userId());
        assertEquals("Reservation approved", secondResource.title());
        assertEquals(NotificationStatus.READ, secondResource.status());

        verify(notificationQueryService, times(1))
                .getNotificationsByUser(userId, pageable);

        verifyNoInteractions(notificationCommandService);
    }

    @Test
    void getNotificationsByUserShouldReturnOkWithEmptyPage() {
        // Arrange
        Long userId = 99L;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Notification> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);

        when(notificationQueryService.getNotificationsByUser(
                userId,
                pageable
        )).thenReturn(emptyPage);

        // Act
        ResponseEntity<Page<NotificationResource>> response =
                notificationController.getNotificationsByUser(
                        userId,
                        pageable
                );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        assertEquals(0, response.getBody().getTotalElements());

        verify(notificationQueryService, times(1))
                .getNotificationsByUser(userId, pageable);

        verifyNoInteractions(notificationCommandService);
    }

    @Test
    void getNotificationByIdShouldReturnOkWhenNotificationExists() {
        // Arrange
        Long notificationId = 10L;
        Long userId = 1L;

        Notification notification = mock(Notification.class);

        when(notification.getId()).thenReturn(notificationId);
        when(notification.getUserId()).thenReturn(userId);
        when(notification.getTitle()).thenReturn("Payment reminder");
        when(notification.getContent())
                .thenReturn("You have a pending payment");
        when(notification.getStatus())
                .thenReturn(NotificationStatus.UNREAD);
        when(notification.getCreatedAt()).thenReturn(null);

        when(notificationQueryService.getNotificationById(notificationId))
                .thenReturn(Optional.of(notification));

        // Act
        ResponseEntity<NotificationResource> response =
                notificationController.getNotificationById(notificationId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        NotificationResource body = response.getBody();

        assertEquals(notificationId, body.id());
        assertEquals(userId, body.userId());
        assertEquals("Payment reminder", body.title());
        assertEquals(
                "You have a pending payment",
                body.content()
        );
        assertEquals(NotificationStatus.UNREAD, body.status());

        verify(notificationQueryService, times(1))
                .getNotificationById(notificationId);

        verifyNoInteractions(notificationCommandService);
    }

    @Test
    void getNotificationByIdShouldReturnNotFoundWhenNotificationDoesNotExist() {
        // Arrange
        Long notificationId = 99L;

        when(notificationQueryService.getNotificationById(notificationId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<NotificationResource> response =
                notificationController.getNotificationById(notificationId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(notificationQueryService, times(1))
                .getNotificationById(notificationId);

        verifyNoInteractions(notificationCommandService);
    }

    @Test
    void markNotificationAsReadShouldReturnOkWithUpdatedNotification() {
        // Arrange
        Long notificationId = 10L;
        Long userId = 1L;

        Notification updatedNotification = mock(Notification.class);

        when(updatedNotification.getId()).thenReturn(notificationId);
        when(updatedNotification.getUserId()).thenReturn(userId);
        when(updatedNotification.getTitle()).thenReturn("Payment reminder");
        when(updatedNotification.getContent())
                .thenReturn("You have a pending payment");
        when(updatedNotification.getStatus())
                .thenReturn(NotificationStatus.READ);
        when(updatedNotification.getCreatedAt()).thenReturn(null);

        when(notificationCommandService.markAsRead(notificationId))
                .thenReturn(updatedNotification);

        // Act
        ResponseEntity<NotificationResource> response =
                notificationController.markNotificationAsRead(notificationId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        NotificationResource body = response.getBody();

        assertEquals(notificationId, body.id());
        assertEquals(userId, body.userId());
        assertEquals("Payment reminder", body.title());
        assertEquals(NotificationStatus.READ, body.status());

        verify(notificationCommandService, times(1))
                .markAsRead(notificationId);

        verifyNoInteractions(notificationQueryService);
    }
}