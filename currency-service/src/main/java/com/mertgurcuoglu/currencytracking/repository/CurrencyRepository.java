package com.mertgurcuoglu.currencytracking.repository;

import com.mertgurcuoglu.currencytracking.model.Currency;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CurrencyRepository extends MongoRepository<Currency, String> {

    Optional<Currency> findBySymbol(String symbol);
}