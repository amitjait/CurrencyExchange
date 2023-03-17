package com.Currency.CurrencyExchange.DTOs.RequestDto;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyExchangeRequest {

    int baseNumber;

    String base;

    String destination;

}
