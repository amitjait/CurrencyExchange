package com.Currency.CurrencyExchange.Service.Implement;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static java.rmi.server.LogStream.log;

@Slf4j //for logs
@Service
public class FetchDataFromExternalAPI {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    HashMap<String, List<Double>> hSet = new HashMap<>();

    private String hashing(){
        hSet.put("USD", new ArrayList<>());
        hSet.put("EUR", new ArrayList<>());
        hSet.put("INR", new ArrayList<>());
        hSet.put("GBP", new ArrayList<>());
        hSet.put("JPY", new ArrayList<>());
        hSet.put("AUD", new ArrayList<>());
        hSet.put("NZD", new ArrayList<>());
        hSet.put("CAD", new ArrayList<>());
        hSet.put("CHF", new ArrayList<>());
        hSet.put("NOK", new ArrayList<>());
        hSet.put("SEK", new ArrayList<>());

        return null;
    }

    String h = hashing();


    public HashMap<String, List<Double>> getPreviousRates() throws IOException {
//        data getting
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        LocalDate date = LocalDate.now();



        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);


        String URL = "https://api.apilayer.com/exchangerates_data/timeseries?start_date="+startDate+"&end_date="+endDate+"&apikey=NQ7Z9TQfNX0X7nNtLRaRrI2ObN4ogRNy";


        HttpGet request = new HttpGet(URL);
        JsonNode response = OBJECT_MAPPER.readTree(httpClient.execute(request).getEntity().getContent());
        httpClient.close();

        JsonNode jsonNode = response.get("rates");
        Iterator<String> fieldNames = jsonNode.fieldNames();
        ArrayList<String> list = new ArrayList<>();

        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode json = jsonNode.get(fieldName);
            Iterator<String> fields = json.fieldNames();
            log.info("Outside "+ fieldName);
            while(fields.hasNext()){
                String innerField = fields.next();

                if(hSet.containsKey(innerField)){
                    log.info("Inside "+json.get(innerField));
                    double rate = json.get(innerField).doubleValue();
                    hSet.get(innerField).add(rate);
                }
            }
            list.add(jsonNode.get(fieldName).toString());
        }
        return hSet;
    }


    public JsonNode getLatestCurrency() throws IOException {

        String helper = "%2C%20";
        String symbols =    "USD" + helper +
                "EUR" + helper +
                "INR" + helper +
                "GBP" + helper +
                "JPY" + helper +
                "AUD" + helper +
                "NZD" + helper +
                "CAD" + helper +
                "CHF" + helper +
                "NOK" + helper +
                "SEK";


        String base = "EUR";

        String URL = "https://api.apilayer.com/exchangerates_data/latest?symbols="+symbols+"&base="+base+"&apikey=NQ7Z9TQfNX0X7nNtLRaRrI2ObN4ogRNy";



        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(URL);
        JsonNode response = OBJECT_MAPPER.readTree(httpClient.execute(request).getEntity().getContent());
        httpClient.close();

        return response.get("rates");
    }
}
