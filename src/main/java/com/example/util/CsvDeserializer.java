package com.example.util;

import com.example.entity.CurrencyTimestamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CsvDeserializer {

    private final String CSV_DELIMITER;

    public CsvDeserializer(String CSV_DELIMITER) {
        this.CSV_DELIMITER = CSV_DELIMITER;
    }

    public List<String> csvToCurrencyCodeList(String csvString, int currencyCodeIndex) throws IOException {
        String line;
        List<String> currencyCodeList = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new StringReader(csvString));

        bf.readLine();
        while ((line = bf.readLine()) != null) {
            String[] values = line.split(CSV_DELIMITER);

            String currencyCode = values[currencyCodeIndex].toUpperCase(Locale.ROOT);

            currencyCodeList.add(currencyCode);
        }
        return currencyCodeList;
    }

    public List<CurrencyTimestamp> csvToCurrencyTimestamp(String csvString,
                                                          int currencyCodeIndex, int relationToEuroIndex, int dateIndex,
                                                          DateTimeFormatter formatter) throws IOException {
        String line;
        List<CurrencyTimestamp> currencyTimestampList = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new StringReader(csvString));

        bf.readLine();
        while ((line = bf.readLine()) != null) {
            String[] values = line.split(CSV_DELIMITER);

            CurrencyTimestamp currencyTimestamp = new CurrencyTimestamp();

            values[relationToEuroIndex] = values[relationToEuroIndex].replace(",", ".");
            currencyTimestamp.setCurrencyCode(values[currencyCodeIndex].toUpperCase(Locale.ROOT));
            currencyTimestamp.setRelationToEuro(Double.parseDouble(values[relationToEuroIndex].replace(",", ".")));
            currencyTimestamp.setDate(LocalDate.parse(values[dateIndex], formatter));

            currencyTimestampList.add(currencyTimestamp);
        }
        return currencyTimestampList;
    }


}
