// DTO class to represent the response from the exchangerate.host API.
package com.mertgurcuoglu.currencytracking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinGeckoResponse extends java.util.HashMap<String, Map<String, Double>> {
}

