package com.Currency.CurrencyExchange.DTOs.ResponceDto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyPredictResponse {

    String predictedValue;

}
