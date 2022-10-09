package org.utk.nikita.example.service;

import org.utk.nikita.example.entity.CurrencyEnum;
import org.utk.nikita.example.service.init.RuCurrencyConversionService;

public interface CurrencyConversionService {
    static CurrencyConversionService getInstance(){
        return new RuCurrencyConversionService();
    }
    double getConversionRatio(CurrencyEnum original, CurrencyEnum target);
}
