package com.Currency.CurrencyExchange.Service.Implement;

import com.Currency.CurrencyExchange.DTOs.RequestDto.CurrencyExchangeRequest;
import com.Currency.CurrencyExchange.DTOs.RequestDto.CurrencyPredictRequest;
import com.Currency.CurrencyExchange.DTOs.ResponceDto.CurrencyExchangeResponse;
import com.Currency.CurrencyExchange.DTOs.ResponceDto.CurrencyPredictResponse;
import com.Currency.CurrencyExchange.Repository.Model.PreviousRatesEntity;
import com.Currency.CurrencyExchange.Repository.CurrencyRepository;
import com.Currency.CurrencyExchange.Service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Slf4j //for logs
@Service
public class CurrencyServiceImp implements CurrencyService {

    @Autowired
    CurrencyRepository currencyRepository;



//    function for currency exchange with CurrencyExchangeRequestDto parameter
    @Override
    public CurrencyExchangeResponse currencyExchange(CurrencyExchangeRequest currencyExchangeRequest) {

//        getting base and destination from request dto
        String base = currencyExchangeRequest.getBase();
        String destination = currencyExchangeRequest.getDestination();

//        getting array after de structuring the base with currency and Currency Number
        String[] baseValue =  deStructureBase(base);
        int baseCount = Integer.parseInt(baseValue[0]);
        String baseCurr = baseValue[1];

//      getting base currency and destination currency rates from Repository
        double baseAmount = currencyRepository.getReferenceById(baseCurr).getRate();
        double destAmount = currencyRepository.getReferenceById(destination).getRate();

//        calculating ratio
        double ratio = destAmount/baseAmount;

//      calculating total amount of destination currency with respect to base currency amount
        double totalDestAmount = baseCount*ratio;

//        creating response params
        String baseResponse = String.valueOf(baseCount)+" "+baseCurr;
        String destinationResponse = String.valueOf(totalDestAmount)+" "+destination;

//        building CurrencyExchangeResponse with base and destination response strings
        CurrencyExchangeResponse currencyExchangeResponse = CurrencyExchangeResponse.builder().base(baseResponse).destination(destinationResponse).build();

//      retuning response

        return currencyExchangeResponse;
    }

//    function to de structure the base ans return a String array
    String[] deStructureBase(String base){
        String[] res = new String[2];

        String newStr = "";

        for(int i=0; i<base.length(); i++){
            if(base.charAt(i) != ' '){
                newStr += base.charAt(i);
            }
        }

        res[0] = newStr.charAt(0)+"";
        res[1] = newStr.substring(1);

        return res;
    }

//    function for predicting and returning the CurrencyPredictResponse
//    method param is CurrencyPredictRequest DTO
    @Override
    public CurrencyPredictResponse currencyPredict(CurrencyPredictRequest currencyPredictRequest) {

//        getting base currency and predict date from currencyPredict Request dto
        String baseCurrency = currencyPredictRequest.getBaseCurrency();
        LocalDate date = currencyPredictRequest.getPredictDate();

//        getting date and list of requested currency history of 30 days from currency repository
        List<PreviousRatesEntity> list = currencyRepository.getReferenceById(baseCurrency).getPreviousRatesEntityList();
        LocalDate currencyDate = currencyRepository.getReferenceById(baseCurrency).getDate();

        Collections.sort(list, (x, y) ->(x.getDate().compareTo(y.getDate()) < 0 )? 1 : 0);


//        calculating average change in currency day by day

        double totalDiff = (list.get(0).getRate()-list.get(29).getRate());


        double changeDiff = totalDiff/list.size();



//        predict the change for given date

        long daysDiff = ChronoUnit.DAYS.between(currencyDate, date);

        double currRate = currencyRepository.getReferenceById(baseCurrency).getRate();

        double ans = 1.0;

//        log.info("ChangeDiff "+changeDiff+" days diff "+ daysDiff+" total diff "+totalDiff+" List size "+list.size()+" curr rate" +currRate);

//        condition to check requested date is after or before from currency date and assigning
//        ans

        if(daysDiff > 0){
            ans = currRate + Math.abs(daysDiff*changeDiff);
        }else if(daysDiff < 0){
            ans = currRate - Math.abs(daysDiff*changeDiff);
        }else{
            ans = currRate;
        }

//        creating string for response DTO
        String predictedValue = String.valueOf(ans)+" "+baseCurrency;
        CurrencyPredictResponse currencyPredictResponse = CurrencyPredictResponse.builder().predictedValue(predictedValue).build();


//        returning response DTO
        return currencyPredictResponse;
    }
}
