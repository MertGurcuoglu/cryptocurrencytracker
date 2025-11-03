package com.mertgurcuoglu.currencytracking.dto;

import java.util.List;

public class SsePayload {
    private final String status; 
    private final List<CurrencyValueDto> currencies;

    public SsePayload(String status, List<CurrencyValueDto> currencies) {
        this.status = status;
        this.currencies = currencies;
    }

    public String getStatus() {
        return status;
    }

    public List<CurrencyValueDto> getCurrencies() {
        return currencies;
    }
}