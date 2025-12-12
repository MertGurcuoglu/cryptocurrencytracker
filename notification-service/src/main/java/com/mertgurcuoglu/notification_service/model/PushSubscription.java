package com.mertgurcuoglu.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//trigger manually
@Document(collection = "pushSubscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushSubscription {
    @Id
    private String id;
    private String userId;
    private String endpoint;
    private String p256dh;
    private String auth;
}