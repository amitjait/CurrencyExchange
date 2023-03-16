package com.Currency.CurrencyExchange.Controllers;

import com.Currency.CurrencyExchange.Model.CurrencyEntity;
import com.Currency.CurrencyExchange.Service.Implement.CurrencyServiceImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RestController
@RequestMapping("currency")
@Slf4j
public class CurrencyController {

    @Autowired
    CurrencyServiceImp currencyServiceImp;

    @GetMapping("/get")
    private String addCurrency(){
        String url = "https://openexchangerates.org/api/latest.json?app_id=2012f43e197147819f368a392dd3cdaf";
        RestTemplate restTemplate = new RestTemplate();

        Object[] currencies = restTemplate.getForObject(url, Object[].class);
        System.out.println(currencies);

        return currencies.toString();
    }

    private static final String BASE_URL = "https://api.apilayer.com/exchangerates_data/convert?to={to}&from={from}&amount={amount}";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @GetMapping("/exchange-rates")
    public ArrayList<String> getExchangeRates() throws IOException {
//        data getting
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        LocalDate date = LocalDate.now();
        String URL = "https://openexchangerates.org/api/historical/"+date+".json?app_id=2012f43e197147819f368a392dd3cdaf";
        HttpGet request = new HttpGet(URL);
        JsonNode response = OBJECT_MAPPER.readTree(httpClient.execute(request).getEntity().getContent());
        httpClient.close();

//        converting Json node to string
        String jsonString = response.get("rates").toString();

        JsonNode jsonNode = response.get("rates");
        Iterator<String> fieldNames = jsonNode.fieldNames();
        ArrayList<String> list = new ArrayList<>();

        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            list.add(String.valueOf(jsonNode.get(fieldName)));
            System.out.println(fieldName + ": " + jsonNode.get(fieldName));
        }
        return list;
    }

}
