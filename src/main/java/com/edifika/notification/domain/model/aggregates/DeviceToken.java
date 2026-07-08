package com.edifika.notification.domain.model.aggregates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceToken {
    private Long id;
    private Long userId;
    private String token;
    private Date createdAt;
    private Date updatedAt;
}
