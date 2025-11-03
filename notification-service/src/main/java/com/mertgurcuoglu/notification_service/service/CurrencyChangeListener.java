package com.mertgurcuoglu.notification_service.service;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.UpdateDescription;
import jakarta.annotation.PostConstruct;
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service
public class CurrencyChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyChangeListener.class);

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final NotificationService notificationService;

    public CurrencyChangeListener(ReactiveMongoTemplate reactiveMongoTemplate, NotificationService notificationService) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        logger.info("MongoDB reaktif change stream dinleyicisi başlatılıyor...");

        reactiveMongoTemplate.changeStream(ChangeStreamEvent.class)
                .watchCollection("currencies")
                .listen()
                .doOnNext(event -> {
                    logger.debug("Change Stream sinyali alındı. Operasyon Tipi: {}", event.getOperationType());

                    ChangeStreamDocument<Document> raw = event.getRaw();
                    if (raw == null) {
                        logger.warn("Gelen event'in 'raw' verisi boş.");
                        return;
                    }

                    String currencySymbol = null;
                    Double newPrice = null;

                    if ("update".equals(raw.getOperationTypeString())) {
                        UpdateDescription updateDescription = raw.getUpdateDescription();
                        if (updateDescription != null && updateDescription.getUpdatedFields() != null) {
                            BsonDocument updatedFields = updateDescription.getUpdatedFields();
                            if (updatedFields.containsKey("currentPrice")) {
                                logger.debug("'currentPrice' içeren bir 'UPDATE' sinyali doğrulandı.");
                                BsonDocument docKey = raw.getDocumentKey();
                                if (docKey != null && docKey.get("_id") != null) {
                                    currencySymbol = docKey.getString("_id").getValue();
                                    newPrice = updatedFields.getDouble("currentPrice").getValue();
                                }
                            }
                        }
                    } else if ("replace".equals(raw.getOperationTypeString())) {
                        Document fullDocument = raw.getFullDocument();
                        if (fullDocument != null && fullDocument.containsKey("currentPrice")) {
                            logger.debug("'currentPrice' içeren bir 'REPLACE' sinyali doğrulandı.");
                            currencySymbol = fullDocument.getString("symbol");
                            Object priceObj = fullDocument.get("currentPrice");
                            if (priceObj instanceof Number) {
                                newPrice = ((Number) priceObj).doubleValue();
                            }
                        }
                    }

                    if (currencySymbol != null && newPrice != null) {
                        logger.info("Fiyat değişikliği ayrıştırıldı, bildirimler gönderiliyor: {} -> ${}", currencySymbol, newPrice);
                        notificationService.sendNotificationsForCurrencyUpdate(currencySymbol, newPrice)
                            .subscribeOn(Schedulers.parallel())
                            .subscribe(
                                null, 
                                error -> logger.error("Bildirim gönderme sürecinde hata oluştu", error)
                            );
                    }
                })
                .doOnError(error -> logger.error("Change Stream dinlemesinde kritik hata!", error))
                .subscribe();

        logger.info("Reaktif dinleyici 'currencies' koleksiyonu için başarıyla abone oldu.");
    }
}

