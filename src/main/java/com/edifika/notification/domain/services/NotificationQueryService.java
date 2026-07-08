package com.edifika.notification.domain.services;

import com.edifika.notification.domain.model.aggregates.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NotificationQueryService {
    Optional<Notification> getNotificationById(Long id);

    Page<Notification> getNotificationsByUser(Long userId, Pageable pageable);
}
