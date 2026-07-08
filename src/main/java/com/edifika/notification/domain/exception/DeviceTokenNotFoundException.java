package com.edifika.notification.domain.exception;

public class DeviceTokenNotFoundException extends RuntimeException {
    public DeviceTokenNotFoundException(String message) {
        super(message);
    }
}
