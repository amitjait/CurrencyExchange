package com.Currency.CurrencyExchange.Controllers;

import com.Currency.CurrencyExchange.Service.Implement.CurrencyServiceImp;
import com.Currency.CurrencyExchange.Service.Implement.FetchDataFromExternalAPI;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("currency")
@Slf4j
public class CurrencyController {

    @Autowired
    CurrencyServiceImp currencyServiceImp;

    @Autowired
    FetchDataFromExternalAPI fetchDataFromExternalAPI;

    @GetMapping("/get")
    private HashMap<String, List<Double>> addCurrency() throws IOException {
        return fetchDataFromExternalAPI.getPreviousRates();
    }



}
