package com.edifika.notification.application.internal.queryservices;

import com.edifika.notification.domain.model.aggregates.Notification;
import com.edifika.notification.domain.model.entities.NotificationEntity;
import com.edifika.notification.domain.model.valueobjects.NotificationStatus;
import com.edifika.notification.infrastructure.persistence.jpa.repositories.JpaNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceImplTest {

    @Mock
    private JpaNotificationRepository jpaNotificationRepository;

    private NotificationQueryServiceImpl notificationQueryService;

    @BeforeEach
    void setUp() {
        notificationQueryService =
                new NotificationQueryServiceImpl(jpaNotificationRepository);
    }

    @Test
    void getNotificationByIdShouldReturnNotificationWhenFound() {

        Long notificationId = 10L;
        Long userId = 1L;

        NotificationEntity entity = mock(NotificationEntity.class);

        when(entity.getId()).thenReturn(notificationId);
        when(entity.getUserId()).thenReturn(userId);
        when(entity.getTitle()).thenReturn("Payment reminder");
        when(entity.getContent()).thenReturn("You have a pending payment");
        when(entity.getStatus()).thenReturn(NotificationStatus.UNREAD);
        when(entity.getCreatedAt()).thenReturn(null);

        when(jpaNotificationRepository.findById(notificationId))
                .thenReturn(Optional.of(entity));


        Optional<Notification> result =
                notificationQueryService.getNotificationById(notificationId);


        assertTrue(result.isPresent());

        Notification notification = result.orElseThrow();

        assertEquals(notificationId, notification.getId());
        assertEquals(userId, notification.getUserId());
        assertEquals("Payment reminder", notification.getTitle());
        assertEquals(
                "You have a pending payment",
                notification.getContent()
        );
        assertEquals(NotificationStatus.UNREAD, notification.getStatus());

        verify(jpaNotificationRepository, times(1))
                .findById(notificationId);

        verifyNoMoreInteractions(jpaNotificationRepository);
    }

    @Test
    void getNotificationByIdShouldReturnEmptyWhenNotFound() {
        Long notificationId = 99L;

        when(jpaNotificationRepository.findById(notificationId))
                .thenReturn(Optional.empty());

        Optional<Notification> result =
                notificationQueryService.getNotificationById(notificationId);

        // Assert
        assertTrue(result.isEmpty());

        verify(jpaNotificationRepository, times(1))
                .findById(notificationId);

        verifyNoMoreInteractions(jpaNotificationRepository);
    }

    @Test
    void getNotificationsByUserShouldReturnMappedNotificationPage() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        NotificationEntity firstEntity = mock(NotificationEntity.class);
        NotificationEntity secondEntity = mock(NotificationEntity.class);

        when(firstEntity.getId()).thenReturn(10L);
        when(firstEntity.getUserId()).thenReturn(userId);
        when(firstEntity.getTitle()).thenReturn("Payment reminder");
        when(firstEntity.getContent())
                .thenReturn("You have a pending payment");
        when(firstEntity.getStatus()).thenReturn(NotificationStatus.UNREAD);
        when(firstEntity.getCreatedAt()).thenReturn(null);

        when(secondEntity.getId()).thenReturn(11L);
        when(secondEntity.getUserId()).thenReturn(userId);
        when(secondEntity.getTitle()).thenReturn("Reservation approved");
        when(secondEntity.getContent())
                .thenReturn("Your reservation was approved");
        when(secondEntity.getStatus()).thenReturn(NotificationStatus.READ);
        when(secondEntity.getCreatedAt()).thenReturn(null);

        Page<NotificationEntity> entityPage = new PageImpl<>(
                List.of(firstEntity, secondEntity),
                pageable,
                2
        );

        when(jpaNotificationRepository.findByUserId(userId, pageable))
                .thenReturn(entityPage);

        // Act
        Page<Notification> result =
                notificationQueryService.getNotificationsByUser(
                        userId,
                        pageable
                );

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        Notification firstNotification = result.getContent().get(0);

        assertEquals(10L, firstNotification.getId());
        assertEquals(userId, firstNotification.getUserId());
        assertEquals("Payment reminder", firstNotification.getTitle());
        assertEquals(NotificationStatus.UNREAD, firstNotification.getStatus());

        Notification secondNotification = result.getContent().get(1);

        assertEquals(11L, secondNotification.getId());
        assertEquals(userId, secondNotification.getUserId());
        assertEquals("Reservation approved", secondNotification.getTitle());
        assertEquals(NotificationStatus.READ, secondNotification.getStatus());

        verify(jpaNotificationRepository, times(1))
                .findByUserId(userId, pageable);

        verifyNoMoreInteractions(jpaNotificationRepository);
    }

    @Test
    void getNotificationsByUserShouldReturnEmptyPageWhenNoNotificationsExist() {
        // Arrange
        Long userId = 99L;
        Pageable pageable = PageRequest.of(0, 10);

        Page<NotificationEntity> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);

        when(jpaNotificationRepository.findByUserId(userId, pageable))
                .thenReturn(emptyPage);

        // Act
        Page<Notification> result =
                notificationQueryService.getNotificationsByUser(
                        userId,
                        pageable
                );

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());

        verify(jpaNotificationRepository, times(1))
                .findByUserId(userId, pageable);

        verifyNoMoreInteractions(jpaNotificationRepository);
    }
}