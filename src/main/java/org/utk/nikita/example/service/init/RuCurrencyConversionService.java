package org.utk.nikita.example.service.init;

import jdk.nashorn.api.scripting.JSObject;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.utk.nikita.example.entity.Currency;
import org.utk.nikita.example.service.CurrencyConversionService;

import javax.print.DocFlavor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RuCurrencyConversionService implements CurrencyConversionService {

    @Override
    public double getConversionRatio(Currency original, Currency target) throws MalformedURLException {
        double originalRatio = getRate(original);
        double targetRatio = getRate(target);
        return originalRatio / targetRatio;
    }
    @SneakyThrows
    private double getRate(Currency currency) throws MalformedURLException {
        if(currency.equals("RUB")){
            return 1;
        }
        URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString());
        return 1;

    }
}
