package com.edifika.notification.interfaces.rest.transform;

import com.edifika.notification.domain.model.aggregates.Notification;
import com.edifika.notification.interfaces.rest.resources.CreateNotificationResource;
import com.edifika.notification.interfaces.rest.resources.NotificationResource;

public class NotificationResourceFromEntityAssembler {

    private NotificationResourceFromEntityAssembler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static NotificationResource toResource(Notification notification) {
        if (notification == null) {
            return null;
        }
        return new NotificationResource(
                notification.getId(),
                notification.getUserId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }

    public static Notification toDomain(CreateNotificationResource resource) {
        if (resource == null) {
            return null;
        }
        return new Notification(
                null,
                resource.userId(),
                resource.title(),
                resource.content(),
                null,
                null
        );
    }
}
