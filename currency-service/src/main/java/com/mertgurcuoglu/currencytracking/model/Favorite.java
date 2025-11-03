package com.mertgurcuoglu.currencytracking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

@Document(collection = "favorites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "user_currency_idx", def = "{'userId' : 1, 'currencyId': 1}", unique = true)
public class Favorite {

    @Id
    private String id;

    private String userId;

    private String currencyId;
}