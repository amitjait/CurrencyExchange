package com.Currency.CurrencyExchange.Service.Implement;


import com.Currency.CurrencyExchange.Repository.Model.CurrencyEntity;
import com.Currency.CurrencyExchange.Repository.Model.PreviousRatesEntity;
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

//    Dependency Injection
    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    PreviousRatesRepository previousRatesRepository;


//    Object of Object mapper
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

//    Hashmap for storing List with currency for future use
    HashMap<String, List<Double>> hMap = new HashMap<>();

    private String hashing(){
        hMap.put("USD", new ArrayList<>());
        hMap.put("EUR", new ArrayList<>());
        hMap.put("INR", new ArrayList<>());
        hMap.put("GBP", new ArrayList<>());
        hMap.put("JPY", new ArrayList<>());
        hMap.put("AUD", new ArrayList<>());
        hMap.put("NZD", new ArrayList<>());
        hMap.put("CAD", new ArrayList<>());
        hMap.put("CHF", new ArrayList<>());
        hMap.put("NOK", new ArrayList<>());
        hMap.put("SEK", new ArrayList<>());

        return null;
    }

//    general calling the function
    String h = hashing();


//    function to create PreviousRates Entity
    public HashMap<String, List<Double>> getPreviousRates() throws IOException {

//        Closeable Htto client object
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

//      start and end date for API
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        String apiKey = "NQ7Z9TQfNX0X7nNtLRaRrI2ObN4ogRNy";
//      API URL
        String URL = "https://api.apilayer.com/exchangerates_data/timeseries?start_date="+startDate+"&end_date="+endDate+"&apikey="+apiKey+"";

//        getting response from API
        HttpGet request = new HttpGet(URL);
        JsonNode response = OBJECT_MAPPER.readTree(httpClient.execute(request).getEntity().getContent());
        httpClient.close();

//        JSONnode object to iterate over it for retrieving data
        JsonNode jsonNode = response.get("rates"); // getting rates only
        Iterator<String> fieldNames = jsonNode.fieldNames();

//        iterating over JSON response
        while (fieldNames.hasNext()) {

            String fieldName = fieldNames.next();

//            getting json node for inner data
            JsonNode json = jsonNode.get(fieldName);

//            iterator to iterate over jSON
            Iterator<String> fields = json.fieldNames();


            while(fields.hasNext()){
                String innerField = fields.next();

//                condition for getting only those value which we have defined or added in the HashMap earlier
                if(hMap.containsKey(innerField)){
//                    log.info("Inside "+json.get(innerField));
                    double rate = json.get(innerField).doubleValue();
                    hMap.get(innerField).add(rate);
                }
            }
        }

//        returning map
        return hMap;
    }

//    function to delete previous data for preventing data redundancy in database

    public void deletePreviousData(){
        if(previousRatesRepository.findAll().size() > 0){
            previousRatesRepository.deleteAll();
            currencyRepository.deleteAll();
        }
    }


//     this is he maon function to fecth data from API and storing it into the databse
    public JsonNode fetchDatAndAddIntoDatabase() throws IOException {

//        function call for deleting previous data if exists
        deletePreviousData();

//        getting map of currency and their list
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


//        API url
        String base = "EUR";

        String URL = "https://api.apilayer.com/exchangerates_data/latest?symbols="+symbols+"&base="+base+"&apikey=NQ7Z9TQfNX0X7nNtLRaRrI2ObN4ogRNy";


//      http client object
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(URL);
        JsonNode response = OBJECT_MAPPER.readTree(httpClient.execute(request).getEntity().getContent());
        httpClient.close();

        JsonNode jsonNode = response.get("rates");
        Iterator<String> fieldNames = jsonNode.fieldNames();

//        Iterate over the JSON object
        while(fieldNames.hasNext()){
            String fieldName = fieldNames.next();

//            List of Previous rate history
            List<Double> rateList = hMap.get(fieldName);
            Double rate = jsonNode.get(fieldName).doubleValue();

//            creating previousRateEntities and getting list
            List<PreviousRatesEntity> previousRatesEntityList = createPreviousRateEntities(rateList, fieldName);

//            creating currency entity
            CurrencyEntity currencyEntity = CurrencyEntity.builder()
                                            .id(fieldName)
                                            .name(fieldName)
                                            .date(LocalDate.now())
                                            .rate(rate)
                                            .previousRatesEntityList(previousRatesEntityList)
                                            .build();

//            assigning or setting currency entity as a parent
            for(PreviousRatesEntity previousRatesEntity : previousRatesEntityList){
                previousRatesEntity.setCurrency(currencyEntity);
            }

//            saving currency entity in the currency repository
            currencyRepository.save(currencyEntity);

        }

//        return response
        return response.get("rates");
    }


//     function for creating the Previous rate Entities
    List<PreviousRatesEntity> createPreviousRateEntities(List<Double> rateList, String currency){

        List<PreviousRatesEntity> list = new ArrayList<>(); // list for storing the PreviousRateEntity


        LocalDate currDate = LocalDate.now();

        int i = 0; //index
        int daysCounter = 29; // daysCounter to assign date to the entity
        int rateListSize = rateList.size();

//        iterating over the rateList and creating the
        while(i < rateListSize){
//            log.info("rate list"+rateListSize+" "+i);

//            getting rate, local date and ID for entity

            Double rate = rateList.get(i);
            LocalDate date = currDate.minusDays(daysCounter);
            String id = currency+String.valueOf(i);

//            creating previousRateEntity
            PreviousRatesEntity previousRatesEntity = PreviousRatesEntity.builder().id(id).rate(rate).date(date).build();

//            adding the entity into the list
            list.add(previousRatesEntity);

//            pointers
            daysCounter--;
            i++;

        }

//        saving all created entities in the previous rates repository
        previousRatesRepository.saveAll(list);
        log.info("List", list.size(), rateList.size());

//        retuning the list

        return list;
    }
}
