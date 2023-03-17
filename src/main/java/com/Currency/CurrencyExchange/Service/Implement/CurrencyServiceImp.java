package com.Currency.CurrencyExchange.Service.Implement;

import com.Currency.CurrencyExchange.DTOs.RequestDto.CurrencyExchangeRequest;
import com.Currency.CurrencyExchange.DTOs.RequestDto.CurrencyPredictRequest;
import com.Currency.CurrencyExchange.DTOs.ResponceDto.CurrencyExchangeResponse;
import com.Currency.CurrencyExchange.DTOs.ResponceDto.CurrencyPredictResponse;
import com.Currency.CurrencyExchange.Model.CurrencyEntity;
import com.Currency.CurrencyExchange.Model.PreviousRatesEntity;
import com.Currency.CurrencyExchange.Repository.CurrencyRepository;
import com.Currency.CurrencyExchange.Service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j //for logs
@Service
public class CurrencyServiceImp implements CurrencyService {

    @Autowired
    CurrencyRepository currencyRepository;


    @Override
    public CurrencyExchangeResponse currencyExchange(CurrencyExchangeRequest currencyExchangeRequest) {
        int baseCount = currencyExchangeRequest.getBaseNumber();
        String base = currencyExchangeRequest.getBase();
        String destination = currencyExchangeRequest.getDestination();

        double baseAmount = currencyRepository.getReferenceById(base).getRate();
        double destAmount = currencyRepository.getReferenceById(destination).getRate();

        double ratio = destAmount/baseAmount;

        double totalDestAmount = baseCount*ratio;

        String baseResponse = String.valueOf(baseCount)+" "+base;
        String destinationResponse = String.valueOf(totalDestAmount)+" "+destination;

        CurrencyExchangeResponse currencyExchangeResponse = CurrencyExchangeResponse.builder().base(baseResponse).destination(destinationResponse).build();


        return currencyExchangeResponse;
    }

    @Override
    public CurrencyPredictResponse currencyPredict(CurrencyPredictRequest currencyPredictRequest) {

        String baseCurrency = currencyPredictRequest.getBaseCurrency();
        LocalDate date = currencyPredictRequest.getPredictDate();

        List<PreviousRatesEntity> list = currencyRepository.getReferenceById(baseCurrency).getPreviousRatesEntityList();
        LocalDate currencyDate = currencyRepository.getReferenceById(baseCurrency).getDate();

        Collections.sort(list, (x, y) ->(x.getDate().compareTo(y.getDate()) < 0 )? 1 : 0);


//        calculating average change in currency day by day

        double totalDiff = (list.get(0).getRate()-list.get(29).getRate());


        double changeDiff = totalDiff/list.size();



//        predict the change for given date

        long daysDiff = ChronoUnit.DAYS.between(date, currencyDate);

        double currRate = currencyRepository.getReferenceById(baseCurrency).getRate();

        double ans = 1.0;

        log.info("ChangeDiff "+changeDiff+" days diff"+ daysDiff+" total diff "+totalDiff+" List size "+list.size()+" curr rate" +currRate);

//        log()
        if(date.compareTo(currencyDate) > 0){
            ans = currRate + (daysDiff*changeDiff);
        }else if(date.compareTo(currencyDate) < 0){
            ans = (daysDiff*changeDiff) - currRate;
        }else{
            ans = currRate;
        }

        String predictedValue = String.valueOf(ans)+" "+baseCurrency;
        CurrencyPredictResponse currencyPredictResponse = CurrencyPredictResponse.builder().predictedValue(predictedValue).build();


        return currencyPredictResponse;
    }
}
