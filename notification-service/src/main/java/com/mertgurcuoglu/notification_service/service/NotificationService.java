package com.mertgurcuoglu.notification_service.service;

import com.mertgurcuoglu.notification_service.client.CurrencyServiceClient;
import com.mertgurcuoglu.notification_service.model.PushSubscription;
import com.mertgurcuoglu.notification_service.repository.PushSubscriptionRepository;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final CurrencyServiceClient currencyServiceClient;
    private final PushService pushService;

    public NotificationService(PushSubscriptionRepository pushSubscriptionRepository,
                               CurrencyServiceClient currencyServiceClient,
                               @Value("${vapid.public.key}") String publicKey,
                               @Value("${vapid.private.key}") String privateKey) throws GeneralSecurityException {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.currencyServiceClient = currencyServiceClient;
        this.pushService = new PushService(publicKey, privateKey);
    }

    public Mono<Void> saveSubscription(PushSubscription subscription) {
        return pushSubscriptionRepository.findByEndpoint(subscription.getEndpoint())
            .switchIfEmpty(Mono.defer(() -> {
                logger.info("Yeni bildirim aboneliği kaydediliyor: UserID={}", subscription.getUserId());
                return pushSubscriptionRepository.save(subscription);
            }))
            .then();
    }
    
    public Mono<Void> sendNotificationsForCurrencyUpdate(String currencySymbol, double newPrice) {
        String payload = "{\"title\":\"Fiyat Uyarısı!\",\"body\":\"%s fiyatı $%.2f oldu!\"}".formatted(currencySymbol, newPrice);
        
        List<String> userIds;
        try {
            userIds = currencyServiceClient.getUserIdsByFavoriteCurrency(currencySymbol);
        } catch (Exception e) {
            logger.error("currency-service'ten favori sahipleri alınamadı! Hata: {}", e.getMessage());
            return Mono.empty();
        }
        
        if (userIds.isEmpty()) {
            logger.info("'{}' için bildirim gönderilecek favori sahibi bulunamadı.", currencySymbol);
            return Mono.empty();
        }

        logger.info("'{}' için bildirim gönderilecek {} kullanıcı bulundu.", currencySymbol, userIds.size());

        return Flux.fromIterable(userIds)
            .flatMap(userId -> {
                logger.debug("Kullanıcı '{}' için bildirim abonelikleri veritabanından aranıyor...", userId);
                return pushSubscriptionRepository.findByUserId(userId)
                    .doOnNext(sub -> logger.debug("Kullanıcı '{}' için abonelik bulundu: Endpoint={}", userId, sub.getEndpoint()));
            })
            .flatMap(subscription -> 
                sendNotification(subscription, payload)
                    .onErrorResume(error -> {
                        logger.warn("Bildirim gönderilemedi: User={}, Endpoint={}. Abonelik siliniyor.", subscription.getUserId(), subscription.getEndpoint(), error);
                        return pushSubscriptionRepository.delete(subscription);
                    })
            )
            .then(); 
    }

    private Mono<Void> sendNotification(PushSubscription sub, String payload) {
        return Mono.fromRunnable(() -> {
            try {
                logger.debug("Web push bildirimi gönderiliyor: User={}, Endpoint={}", sub.getUserId(), sub.getEndpoint());
                pushService.send(new Notification(sub.getEndpoint(), sub.getP256dh(), sub.getAuth(), payload));
                logger.debug("Web push bildirimi başarıyla gönderildi: User={}", sub.getUserId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}