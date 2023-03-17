package com.Currency.CurrencyExchange.Controllers;

import com.Currency.CurrencyExchange.Service.Implement.FetchDataFromExternalAPI;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class DataFetchingController {

    @Autowired
    FetchDataFromExternalAPI fetchDataFromExternalAPI;

    @PostConstruct()
    private JsonNode addData() throws IOException {
        return fetchDataFromExternalAPI.fetchDatAndAddIntoDatabase();
    }
}
