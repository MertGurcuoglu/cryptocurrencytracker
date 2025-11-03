package com.mertgurcuoglu.currencytracking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinGeckoMarketsResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("current_price")
    private double currentPrice;

    @JsonProperty("max_supply")
    private Long maxSupply;

    @JsonProperty("market_cap")
    private Long marketCap;

    @JsonProperty("total_volume")
    private Long totalVolume;

    @JsonProperty("price_change_percentage_24h")
    private Double priceChangePercentage24h;

    @JsonProperty("circulating_supply")
    private Double circulatingSupply;


    public CoinGeckoMarketsResponse() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public Long getMaxSupply() { return maxSupply; }
    public void setMaxSupply(Long maxSupply) { this.maxSupply = maxSupply; }
    
    public Long getMarketCap() { return marketCap; }
    public void setMarketCap(Long marketCap) { this.marketCap = marketCap; }
    public Long getTotalVolume() { return totalVolume; }
    public void setTotalVolume(Long totalVolume) { this.totalVolume = totalVolume; }
    public Double getPriceChangePercentage24h() { return priceChangePercentage24h; }
    public void setPriceChangePercentage24h(Double priceChangePercentage24h) { this.priceChangePercentage24h = priceChangePercentage24h; }
    public Double getCirculatingSupply() { return circulatingSupply; }
    public void setCirculatingSupply(Double circulatingSupply) { this.circulatingSupply = circulatingSupply; }
}

