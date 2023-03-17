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


        String base = currencyExchangeRequest.getBase();
        String[] baseValue =  deStructureBase(base);

        int baseCount = Integer.parseInt(baseValue[0]);
        String baseCurr = baseValue[1];

        String destination = currencyExchangeRequest.getDestination();

        double baseAmount = currencyRepository.getReferenceById(baseCurr).getRate();
        double destAmount = currencyRepository.getReferenceById(destination).getRate();

        double ratio = destAmount/baseAmount;

        double totalDestAmount = baseCount*ratio;

        String baseResponse = String.valueOf(baseCount)+" "+baseCurr;
        String destinationResponse = String.valueOf(totalDestAmount)+" "+destination;

        CurrencyExchangeResponse currencyExchangeResponse = CurrencyExchangeResponse.builder().base(baseResponse).destination(destinationResponse).build();


        return currencyExchangeResponse;
    }

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

        long daysDiff = ChronoUnit.DAYS.between(currencyDate, date);

        double currRate = currencyRepository.getReferenceById(baseCurrency).getRate();

        double ans = 1.0;

        log.info("ChangeDiff "+changeDiff+" days diff "+ daysDiff+" total diff "+totalDiff+" List size "+list.size()+" curr rate" +currRate);

//        log()
        if(daysDiff > 0){
            ans = currRate + Math.abs(daysDiff*changeDiff);
        }else if(daysDiff < 0){
            ans = currRate - Math.abs(daysDiff*changeDiff);
        }else{
            ans = currRate;
        }

        String predictedValue = String.valueOf(ans)+" "+baseCurrency;
        CurrencyPredictResponse currencyPredictResponse = CurrencyPredictResponse.builder().predictedValue(predictedValue).build();


        return currencyPredictResponse;
    }
}
