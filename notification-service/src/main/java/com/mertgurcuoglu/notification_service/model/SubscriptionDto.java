package com.mertgurcuoglu.notification_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriptionDto {

    private String endpoint;
    private Keys keys;

    @Data
    @NoArgsConstructor
    public static class Keys {
        private String p256dh;
        private String auth;
    }
}

