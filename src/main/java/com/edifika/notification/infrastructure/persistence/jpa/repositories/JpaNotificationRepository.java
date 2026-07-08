package com.edifika.notification.infrastructure.persistence.jpa.repositories;

import com.edifika.notification.domain.model.entities.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findByUserId(Long userId, Pageable pageable);
}

