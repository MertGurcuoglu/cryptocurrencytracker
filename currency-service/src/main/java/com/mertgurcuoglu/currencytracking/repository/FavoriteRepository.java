package com.mertgurcuoglu.currencytracking.repository;

import com.mertgurcuoglu.currencytracking.model.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {

    List<Favorite> findByUserId(String userId);

    Optional<Favorite> findByUserIdAndCurrencyId(String userId, String currencyId);

    void deleteByUserIdAndCurrencyId(String userId, String currencyId);

    List<Favorite> findByCurrencyId(String currencyId);
}