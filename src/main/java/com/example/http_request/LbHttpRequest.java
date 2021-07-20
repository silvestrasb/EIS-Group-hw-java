package com.example.http_request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LbHttpRequest {


    public static final String LB_GET_CURRENCY_TIMESTAMPS = "https://www.lb.lt/lt/currency/exportlist/?csv=1&currency=%s&ff=1&class=Eu&type=day&date_from_day=%s&date_to_day=%s";
    public static final String LB_GET_CURRENCY_CODES = "https://www.lb.lt/lt/currency/daylyexport/?csv=1&class=Eu";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public static String getCurrencyCodes() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Accept", "text/html")
                .uri(URI.create(LB_GET_CURRENCY_CODES))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String getCsvCurrencyTimestamps(String currencyCode, LocalDate fromDate, LocalDate toDate)
            throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        String paramUrl = String.format(LB_GET_CURRENCY_TIMESTAMPS, currencyCode, fromDate, toDate);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("Accept", "text/html")
                .uri(URI.create(paramUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

}
