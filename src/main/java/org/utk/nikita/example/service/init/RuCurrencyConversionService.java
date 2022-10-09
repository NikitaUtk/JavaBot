package org.utk.nikita.example.service.init;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.utk.nikita.example.entity.CurrencyEnum;
import org.utk.nikita.example.entity.Currency;
import org.utk.nikita.example.service.CurrencyConversionService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class RuCurrencyConversionService implements CurrencyConversionService {

    @Override
    public double getConversionRatio(CurrencyEnum original, CurrencyEnum target) {
        double originalRatio = getRate(original);
        double targetRatio = getRate(target);
        return originalRatio / targetRatio;

    }
    @SneakyThrows
    private double getRate(CurrencyEnum currencyEnumE){
        if(currencyEnumE.equals(CurrencyEnum.RUB)){
            return 1;
        }
        String s = String.valueOf(currencyEnumE);
        URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
        InputStream inputStream = url.openStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String json = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        // Парсит JSON и получает данные валют.
        JSONObject currenciesData = new JSONObject(json)
                .getJSONObject("Valute");
        Map<String, Double> newMap = new HashMap<>();
        Currency cur = new Currency();

        // Получает коды валют из полученных данных.
        List<Currency> currencies = currenciesData.keySet()
                // Преобразовывает Set в Stream.
                .stream()
                // При помощи GSON переводит JSON строчку валюты к классу Currency.
                .map((currency) ->
                        new Gson().fromJson(
                                currenciesData.getJSONObject(currency)
                                        .toString(),
                                Currency.class
                        )
                )
                // Преобразовывает Stream в List.
                .collect(toList());

        Double Value = currencies.stream().filter(ret-> ret.getCharCode().equals(s)).mapToDouble(n -> n.getValue()).sum();
        Double Nominal = currencies.stream().filter(ret-> ret.getCharCode().equals(s)).mapToDouble(n -> n.getNominal()).sum();

        return Value/Nominal;
    }
}