package com.edifika.notification.domain.model.entities;

import com.edifika.shared.domain.model.entity.AuditableModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "device_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTokenEntity extends AuditableModel {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;
}
