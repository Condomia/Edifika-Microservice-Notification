package com.edifika.notification.application.internal.commandservices;

import com.edifika.notification.domain.exception.NotificationNotFoundException;
import com.edifika.notification.domain.model.aggregates.Notification;
import com.edifika.notification.domain.model.entities.NotificationEntity;
import com.edifika.notification.domain.model.valueobjects.NotificationStatus;
import com.edifika.notification.domain.services.DeviceTokenQueryService;
import com.edifika.notification.domain.services.FirebaseNotificationGateway;
import com.edifika.notification.domain.services.NotificationCommandService;
import com.edifika.notification.infrastructure.persistence.jpa.repositories.JpaNotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final JpaNotificationRepository jpaNotificationRepository;
    private final DeviceTokenQueryService deviceTokenQueryService;
    private final FirebaseNotificationGateway firebaseNotificationGateway;

    public NotificationCommandServiceImpl(JpaNotificationRepository jpaNotificationRepository,
                                         DeviceTokenQueryService deviceTokenQueryService,
                                         FirebaseNotificationGateway firebaseNotificationGateway) {
        this.jpaNotificationRepository = jpaNotificationRepository;
        this.deviceTokenQueryService = deviceTokenQueryService;
        this.firebaseNotificationGateway = firebaseNotificationGateway;
    }

    @Override
    public Notification createNotification(Long userId, String title, String content) {
        NotificationEntity entity = new NotificationEntity();
        entity.setUserId(userId);
        entity.setTitle(title);
        entity.setContent(content);
        entity.setStatus(NotificationStatus.UNREAD);

        NotificationEntity savedEntity = jpaNotificationRepository.save(entity);

        // Obtener token del dispositivo y enviar push via Firebase (si existe)
        deviceTokenQueryService.getTokenByUserId(userId)
                .ifPresent(deviceToken ->
                        firebaseNotificationGateway.sendPushNotification(
                                deviceToken.getToken(), title, content
                        )
                );

        return mapToDomain(savedEntity);
    }

    @Override
    public Notification markAsRead(Long id) {
        NotificationEntity entity = jpaNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification with id " + id + " not found"));

        entity.setStatus(NotificationStatus.READ);
        NotificationEntity updatedEntity = jpaNotificationRepository.save(entity);
        return mapToDomain(updatedEntity);
    }

    private Notification mapToDomain(NotificationEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
