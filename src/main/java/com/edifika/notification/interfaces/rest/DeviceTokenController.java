package com.edifika.notification.interfaces.rest;

import com.edifika.notification.domain.model.aggregates.DeviceToken;
import com.edifika.notification.domain.services.DeviceTokenCommandService;
import com.edifika.notification.domain.services.DeviceTokenQueryService;
import com.edifika.notification.interfaces.rest.resources.CreateDeviceTokenResource;
import com.edifika.notification.interfaces.rest.resources.DeviceTokenResource;
import com.edifika.notification.interfaces.rest.transform.DeviceTokenResourceFromEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device-tokens")
public class DeviceTokenController {

    private final DeviceTokenCommandService deviceTokenCommandService;
    private final DeviceTokenQueryService deviceTokenQueryService;

    public DeviceTokenController(DeviceTokenCommandService deviceTokenCommandService,
                                 DeviceTokenQueryService deviceTokenQueryService) {
        this.deviceTokenCommandService = deviceTokenCommandService;
        this.deviceTokenQueryService = deviceTokenQueryService;
    }

    @PostMapping
    public ResponseEntity<DeviceTokenResource> registerToken(@RequestBody CreateDeviceTokenResource resource) {
        DeviceToken deviceToken = deviceTokenCommandService.registerToken(
                resource.userId(),
                resource.token()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DeviceTokenResourceFromEntityAssembler.toResource(deviceToken));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DeviceTokenResource> getTokenByUserId(@PathVariable Long userId) {
        return deviceTokenQueryService.getTokenByUserId(userId)
                .map(deviceToken -> ResponseEntity.ok(DeviceTokenResourceFromEntityAssembler.toResource(deviceToken)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceTokenResource> getTokenById(@PathVariable Long id) {
        return deviceTokenQueryService.getTokenById(id)
                .map(deviceToken -> ResponseEntity.ok(DeviceTokenResourceFromEntityAssembler.toResource(deviceToken)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
