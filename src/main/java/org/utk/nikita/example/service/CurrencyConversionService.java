package org.utk.nikita.example.service;

import org.utk.nikita.example.entity.Currency;
import org.utk.nikita.example.service.init.RuCurrencyConversionService;

import java.net.MalformedURLException;

public interface CurrencyConversionService {
    //static CurrencyConversionService getInstance(return new RuCurrencyConversionService();)

    double getConversionRatio(Currency original, Currency target) throws MalformedURLException;
}
