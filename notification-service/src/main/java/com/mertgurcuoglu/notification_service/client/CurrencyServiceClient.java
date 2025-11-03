package com.mertgurcuoglu.notification_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "currency-service")
public interface CurrencyServiceClient {

    
    @GetMapping("/api/favorites/internal/by-currency/{currencyId}")
    List<String> getUserIdsByFavoriteCurrency(@PathVariable("currencyId") String currencyId);
}