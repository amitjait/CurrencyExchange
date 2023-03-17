package com.Currency.CurrencyExchange.DTOs.RequestDto;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyExchangeRequest {


    String base;

    String destination;

}
