package com.Currency.CurrencyExchange.Repository;

import com.Currency.CurrencyExchange.Model.PreviousRatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreviousRatesRepository extends JpaRepository<PreviousRatesEntity, Integer> {
}
