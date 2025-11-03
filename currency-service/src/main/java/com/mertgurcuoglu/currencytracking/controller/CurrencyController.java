package com.mertgurcuoglu.currencytracking.controller;

import com.mertgurcuoglu.currencytracking.dto.CurrencyValueDto;
import com.mertgurcuoglu.currencytracking.model.Currency;
import com.mertgurcuoglu.currencytracking.service.CurrencyTracking;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final CurrencyTracking service;
    public CurrencyController(CurrencyTracking service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Currency> create(@RequestBody Currency currency) {
        Currency createdCurrency = service.create(currency);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCurrency);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Currency> update(@PathVariable String id, @RequestBody Currency updated) {
        Currency updatedCurrency = service.update(id, updated);
        return ResponseEntity.ok(updatedCurrency);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public Currency getById(@PathVariable String id) {
        return service.getById(id);
    }
    @GetMapping
    public List<Currency> getAll() {
        return service.getAll();
    }
    @GetMapping("/all-values")
    public List<CurrencyValueDto> getAllCurrentValues() {
        return service.getAllCurrencyValues();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return service.subscribe();
    }
    
}
