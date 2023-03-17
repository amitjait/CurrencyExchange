package com.Currency.CurrencyExchange.Repository;

import com.Currency.CurrencyExchange.Repository.Model.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {

}
