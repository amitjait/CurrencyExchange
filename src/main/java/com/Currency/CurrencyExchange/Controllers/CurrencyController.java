package com.Currency.CurrencyExchange.Controllers;

import com.Currency.CurrencyExchange.DTOs.RequestDto.CurrencyExchangeRequest;
import com.Currency.CurrencyExchange.DTOs.RequestDto.CurrencyPredictRequest;
import com.Currency.CurrencyExchange.DTOs.ResponceDto.CurrencyExchangeResponse;
import com.Currency.CurrencyExchange.DTOs.ResponceDto.CurrencyPredictResponse;
import com.Currency.CurrencyExchange.Repository.CurrencyRepository;
import com.Currency.CurrencyExchange.Service.CurrencyService;
import com.Currency.CurrencyExchange.Service.Implement.CurrencyServiceImp;
import com.Currency.CurrencyExchange.Service.Implement.FetchDataFromExternalAPI;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("currency")
@Slf4j
public class CurrencyController {
    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    CurrencyServiceImp currencyServiceImp;

    @GetMapping("/exchange")
    private ResponseEntity currencyExchange(@RequestBody()CurrencyExchangeRequest currencyExchangeRequest){

        CurrencyExchangeResponse currencyExchangeResponse = currencyServiceImp.currencyExchange(currencyExchangeRequest);

        return new ResponseEntity<>(currencyExchangeResponse, HttpStatus.OK);
    }

    @GetMapping("/predict")
    private ResponseEntity currencyPredict(@RequestBody()CurrencyPredictRequest currencyPredictRequest){

        CurrencyPredictResponse currencyPredictResponse = currencyServiceImp.currencyPredict(currencyPredictRequest);

        return new ResponseEntity<>(currencyPredictResponse, HttpStatus.OK);
    }





}
