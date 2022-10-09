package org.utk.nikita.example.service.init;

import org.utk.nikita.example.entity.CurrencyEnum;
import org.utk.nikita.example.service.CurrencyModeService;

import java.util.HashMap;
import java.util.Map;

public class HashMapCurrencyModeService implements CurrencyModeService {
    private final Map<Long, CurrencyEnum> originalCurrency = new HashMap<>();
    private final Map<Long, CurrencyEnum> targetCurrency = new HashMap<>();

    public HashMapCurrencyModeService() {
        System.out.println("HASHMAP MODE is created");
    }

    @Override
    public CurrencyEnum getOriginalCurrency(long chatId) {
        return originalCurrency.get(chatId);
    }

    @Override
    public CurrencyEnum getTargetCurrency(long chatId) {
        return targetCurrency.get(chatId);
    }

    @Override
    public void setOriginalCurrency(long chatId, CurrencyEnum currencyEnum) {
        originalCurrency.put(chatId, currencyEnum);
    }

    @Override
    public void setTargetCurrency(long chatId, CurrencyEnum currencyEnum) {
        targetCurrency.put(chatId, currencyEnum);
  }
}
