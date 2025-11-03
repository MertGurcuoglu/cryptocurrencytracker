package com.mertgurcuoglu.currencytracking.controller;

import com.mertgurcuoglu.currencytracking.service.CurrencyTracking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final CurrencyTracking service;

    public FavoriteController(CurrencyTracking service) {
        this.service = service;
    }
    @GetMapping
    public ResponseEntity<List<String>> getUserFavorites(@RequestHeader("X-Authenticated-User-Id") String userId) {
        List<String> favoriteIds = service.getUserFavoriteCurrencyIds(userId);
        return ResponseEntity.ok(favoriteIds);
    }

    @PostMapping
    public ResponseEntity<Void> addFavorite(@RequestHeader("X-Authenticated-User-Id") String userId,
                                            @RequestBody Map<String, String> payload) {
        String currencyId = payload.get("currencyId");
        service.addFavorite(userId, currencyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{currencyId}")
    public ResponseEntity<Void> removeFavorite(@RequestHeader("X-Authenticated-User-Id") String userId,
                                               @PathVariable String currencyId) {
        service.removeFavorite(userId, currencyId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/internal/by-currency/{currencyId}")
public List<String> getUserIdsByFavoriteCurrency(@PathVariable String currencyId) {
        return service.getUserIdsByFavoriteCurrency(currencyId);
  }

}