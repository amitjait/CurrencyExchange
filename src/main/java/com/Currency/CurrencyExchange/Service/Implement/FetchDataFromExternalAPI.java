package com.Currency.CurrencyExchange.Service.Implement;


import com.Currency.CurrencyExchange.Model.CurrencyEntity;
import com.Currency.CurrencyExchange.Model.PreviousRatesEntity;
import com.Currency.CurrencyExchange.Repository.CurrencyRepository;
import com.Currency.CurrencyExchange.Repository.PreviousRatesRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;



@Slf4j //for logs
@Service
public class FetchDataFromExternalAPI {

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    PreviousRatesRepository previousRatesRepository;


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
//        LocalDate date = LocalDate.now();



        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);


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
//            log.info("Outside "+ fieldName);
            while(fields.hasNext()){
                String innerField = fields.next();

                if(hSet.containsKey(innerField)){
//                    log.info("Inside "+json.get(innerField));
                    double rate = json.get(innerField).doubleValue();
                    hSet.get(innerField).add(rate);
                }
            }
            list.add(jsonNode.get(fieldName).toString());
        }
        return hSet;
    }

    public void deletePreviousData(){
        if(previousRatesRepository.findAll().size() > 0){
            previousRatesRepository.deleteAll();
            currencyRepository.deleteAll();
        }
    }


    public JsonNode fetchDatAndAddIntoDatabase() throws IOException {

        deletePreviousData();

        HashMap<String, List<Double>> hMap = getPreviousRates();

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

        JsonNode jsonNode = response.get("rates");
        Iterator<String> fieldNames = jsonNode.fieldNames();

        while(fieldNames.hasNext()){
            String fieldName = fieldNames.next();
            List<Double> rateList = hMap.get(fieldName);
            Double rate = jsonNode.get(fieldName).doubleValue();

            List<PreviousRatesEntity> previousRatesEntityList = createPreviousRateEntities(rateList, fieldName);

            CurrencyEntity currencyEntity = CurrencyEntity.builder()
                                            .id(fieldName)
                                            .name(fieldName)
                                            .date(LocalDate.now())
                                            .rate(rate)
                                            .previousRatesEntityList(previousRatesEntityList)
                                            .build();

            for(PreviousRatesEntity previousRatesEntity : previousRatesEntityList){
                previousRatesEntity.setCurrency(currencyEntity);
            }

            currencyRepository.save(currencyEntity);

        }

        return response.get("rates");
    }

    List<PreviousRatesEntity> createPreviousRateEntities(List<Double> rateList, String currency){
        List<PreviousRatesEntity> list = new ArrayList<>();

        LocalDate currDate = LocalDate.now();

        int i = 0;
        int daysCounter = 29;
        int rateListSize = rateList.size();

        while(i < rateListSize){
            log.info("rate list"+rateListSize+" "+i);
            Double rate = rateList.get(i);
            LocalDate date = currDate.minusDays(daysCounter);
            String id = currency+String.valueOf(i);
            PreviousRatesEntity previousRatesEntity = PreviousRatesEntity.builder().id(id).rate(rate).date(date).build();

            list.add(previousRatesEntity);

            daysCounter--;
            i++;

        }

        previousRatesRepository.saveAll(list);
        log.info("List", list.size(), rateList.size());

        return list;
    }
}
