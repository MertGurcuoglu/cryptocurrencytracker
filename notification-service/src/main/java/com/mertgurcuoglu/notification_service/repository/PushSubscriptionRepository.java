package com.mertgurcuoglu.notification_service.repository;

import com.mertgurcuoglu.notification_service.model.PushSubscription;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PushSubscriptionRepository extends ReactiveMongoRepository<PushSubscription, String> {

    Flux<PushSubscription> findByUserId(String userId);

    Mono<PushSubscription> findByEndpoint(String endpoint);
}
