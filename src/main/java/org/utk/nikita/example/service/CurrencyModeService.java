package org.utk.nikita.example.service;

import org.utk.nikita.example.entity.CurrencyEnum;
import org.utk.nikita.example.service.init.HashMapCurrencyModeService;


public interface CurrencyModeService {
    static CurrencyModeService getInstance() {
        return new HashMapCurrencyModeService();
    }

    CurrencyEnum getOriginalCurrency(long chatId);

    CurrencyEnum getTargetCurrency(long chatId);

    void setOriginalCurrency(long chatId, CurrencyEnum currencyEnum);

    void setTargetCurrency(long chatId, CurrencyEnum currencyEnum);

}
