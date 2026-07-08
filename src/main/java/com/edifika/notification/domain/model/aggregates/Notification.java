package com.edifika.notification.domain.model.aggregates;

import com.edifika.notification.domain.model.valueobjects.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private NotificationStatus status;
    private Date createdAt;
}
