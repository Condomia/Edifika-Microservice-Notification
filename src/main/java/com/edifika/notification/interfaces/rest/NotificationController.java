package com.edifika.notification.interfaces.rest;

import com.edifika.notification.domain.services.NotificationCommandService;
import com.edifika.notification.domain.services.NotificationQueryService;
import com.edifika.notification.interfaces.rest.resources.CreateNotificationResource;
import com.edifika.notification.interfaces.rest.resources.NotificationResource;
import com.edifika.notification.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    public NotificationController(NotificationCommandService notificationCommandService,
                                  NotificationQueryService notificationQueryService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @PostMapping
    public ResponseEntity<NotificationResource> createNotification(@RequestBody CreateNotificationResource resource) {
        var notification = notificationCommandService.createNotification(
                resource.userId(),
                resource.title(),
                resource.content()
        );
        NotificationResource response = NotificationResourceFromEntityAssembler.toResource(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResource>> getNotificationsByUser(
            @PathVariable Long userId,
            @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<NotificationResource> resources = notificationQueryService.getNotificationsByUser(userId, pageable)
                .map(NotificationResourceFromEntityAssembler::toResource);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResource> getNotificationById(@PathVariable Long id) {
        return notificationQueryService.getNotificationById(id)
                .map(notification -> {
                    NotificationResource resource = NotificationResourceFromEntityAssembler.toResource(notification);
                    return ResponseEntity.ok(resource);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResource> markNotificationAsRead(@PathVariable Long id) {
        var updatedNotification = notificationCommandService.markAsRead(id);
        NotificationResource response = NotificationResourceFromEntityAssembler.toResource(updatedNotification);
        return ResponseEntity.ok(response);
    }
}
