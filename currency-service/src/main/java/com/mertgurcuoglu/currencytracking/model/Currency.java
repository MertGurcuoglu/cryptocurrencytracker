package com.mertgurcuoglu.currencytracking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
//trigger manually

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "currencies")
public class Currency {

    @Id
    private String id;
    private String fullName;
    private String symbol;
    private Long maxSupply;
    private double currentPrice;
    private String logoUrl;
    private Long marketCap;
    private Long totalVolume;
    private Double priceChangePercentage24h;
    private Double circulatingSupply;
}