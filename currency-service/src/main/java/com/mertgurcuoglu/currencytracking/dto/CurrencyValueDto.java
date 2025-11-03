package com.mertgurcuoglu.currencytracking.dto;

public class CurrencyValueDto {
    private String id;
    private String fullName;
    private String symbol;
    private double currentPrice;
    private String logoUrl; 
    private Long marketCap;
    private Long totalVolume;
    private Double priceChangePercentage24h;
    private Double circulatingSupply;

   
     public CurrencyValueDto(String id, String fullName, String symbol, double currentPrice, String logoUrl,
                            Long marketCap, Long totalVolume, Double priceChangePercentage24h, Double circulatingSupply) {
        this.id = id;
        this.fullName = fullName;
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.logoUrl = logoUrl;
        this.marketCap = marketCap;
        this.totalVolume = totalVolume;
        this.priceChangePercentage24h = priceChangePercentage24h;
        this.circulatingSupply = circulatingSupply;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getSymbol() { return symbol; }
    public double getCurrentPrice() { return currentPrice; }
    public String getLogoUrl() { return logoUrl; } 
    public Long getMarketCap() { return marketCap; }
    public Long getTotalVolume() { return totalVolume; }
    public Double getPriceChangePercentage24h() { return priceChangePercentage24h; }
    public Double getCirculatingSupply() { return circulatingSupply; }
}

