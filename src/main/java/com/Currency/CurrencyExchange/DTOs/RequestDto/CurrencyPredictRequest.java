package com.Currency.CurrencyExchange.DTOs.RequestDto;


import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyPredictRequest {

    String baseCurrency;
    LocalDate predictDate;

}
