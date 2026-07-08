package com.edifika.notification.application.internal.queryservices;

import com.edifika.notification.domain.model.aggregates.Notification;
import com.edifika.notification.domain.model.entities.NotificationEntity;
import com.edifika.notification.domain.services.NotificationQueryService;
import com.edifika.notification.infrastructure.persistence.jpa.repositories.JpaNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final JpaNotificationRepository jpaNotificationRepository;

    public NotificationQueryServiceImpl(JpaNotificationRepository jpaNotificationRepository) {
        this.jpaNotificationRepository = jpaNotificationRepository;
    }

    @Override
    public Optional<Notification> getNotificationById(Long id) {
        return jpaNotificationRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public Page<Notification> getNotificationsByUser(Long userId, Pageable pageable) {
        return jpaNotificationRepository.findByUserId(userId, pageable)
                .map(this::mapToDomain);
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
