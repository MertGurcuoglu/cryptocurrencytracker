package com.mertgurcuoglu.notification_service.controller;

import com.mertgurcuoglu.notification_service.model.PushSubscription;
import com.mertgurcuoglu.notification_service.model.SubscriptionDto; 
import com.mertgurcuoglu.notification_service.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> subscribe(@RequestHeader("X-Authenticated-User-Id") String userId,
                                @RequestBody SubscriptionDto dto) { 
        PushSubscription subscription = new PushSubscription();
        subscription.setUserId(userId);
        subscription.setEndpoint(dto.getEndpoint());
        subscription.setP256dh(dto.getKeys().getP256dh());
        subscription.setAuth(dto.getKeys().getAuth());

        return notificationService.saveSubscription(subscription);
    }
}

