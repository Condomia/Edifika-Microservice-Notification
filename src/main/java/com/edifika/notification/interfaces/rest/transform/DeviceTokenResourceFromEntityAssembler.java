package com.edifika.notification.interfaces.rest.transform;

import com.edifika.notification.domain.model.aggregates.DeviceToken;
import com.edifika.notification.interfaces.rest.resources.CreateDeviceTokenResource;
import com.edifika.notification.interfaces.rest.resources.DeviceTokenResource;

public class DeviceTokenResourceFromEntityAssembler {

    private DeviceTokenResourceFromEntityAssembler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static DeviceTokenResource toResource(DeviceToken deviceToken) {
        if (deviceToken == null) {
            return null;
        }
        return new DeviceTokenResource(
                deviceToken.getId(),
                deviceToken.getUserId(),
                deviceToken.getToken(),
                deviceToken.getCreatedAt(),
                deviceToken.getUpdatedAt()
        );
    }

    public static DeviceToken toDomain(CreateDeviceTokenResource resource) {
        if (resource == null) {
            return null;
        }
        return new DeviceToken(
                null,
                resource.userId(),
                resource.token(),
                null,
                null
        );
    }
}
