package com.Currency.CurrencyExchange.Service;

import com.Currency.CurrencyExchange.DTOs.RequestDto.CurrencyExchangeRequest;
import com.Currency.CurrencyExchange.DTOs.RequestDto.CurrencyPredictRequest;
import com.Currency.CurrencyExchange.DTOs.ResponceDto.CurrencyExchangeResponse;
import com.Currency.CurrencyExchange.DTOs.ResponceDto.CurrencyPredictResponse;

public interface CurrencyService {

    CurrencyExchangeResponse currencyExchange(CurrencyExchangeRequest currencyExchangeRequest);

    CurrencyPredictResponse currencyPredict(CurrencyPredictRequest currencyPredictRequest);

}
