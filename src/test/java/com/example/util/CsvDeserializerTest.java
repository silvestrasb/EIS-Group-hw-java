package com.example.util;


import com.example.entity.CurrencyTimestamp;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsvDeserializerTest {

    private CsvDeserializer deserializer = new CsvDeserializer(";");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Test
    public void csvToCurrencyCodeList_ValidString_ValidOutputList() throws IOException {
        // Given
        List<String> currencyCodeList = List.of("AUD", "EUR", "USD", "RUB");
        String CsvCurrencyCodeString = "CODE;VALUE\nAUD;1\nEUR;2\nUSD;3\nRUB;0";

        // When
        List<String> deserializedCurrencyCodeList = deserializer.csvToCurrencyCodeList(CsvCurrencyCodeString, 0);

        // Then
        assertEquals(deserializedCurrencyCodeList.get(2), currencyCodeList.get(2));
        assertEquals(deserializedCurrencyCodeList.get(1), currencyCodeList.get(1));
    }

    @Test
    public void csvToCurrencyTimestampValidString_ValidOutputList() throws IOException {
        // Given
        List<CurrencyTimestamp> currencyTimestampList = new ArrayList<>();

        currencyTimestampList.add(
                new CurrencyTimestamp("USD", 23.43,
                        LocalDate.of(2020, 07, 01)));
        currencyTimestampList.add(
                new CurrencyTimestamp("USD", 30.5,
                        LocalDate.of(2020, 07, 02))
        );

        String CsvCurrencyTimestampString = "CURRENCY_CODE;VALUE;DATE\nUSD;23.43;2020-07-01;\nUSD;30.5;2020-07-02";

        // When
        List<CurrencyTimestamp> deserializedCurrencyTimestampList =
                deserializer.csvToCurrencyTimestamp(CsvCurrencyTimestampString, 0, 1, 2,
                        formatter);

        // Then

        Collections.sort(deserializedCurrencyTimestampList, Comparator.comparing(CurrencyTimestamp::getDate));
        Collections.sort(currencyTimestampList, Comparator.comparing(CurrencyTimestamp::getDate));

        assertEquals(deserializedCurrencyTimestampList.get(0).getRelationToEuro(),
                currencyTimestampList.get(0).getRelationToEuro());
        assertEquals(deserializedCurrencyTimestampList.get(1).getRelationToEuro(),
                currencyTimestampList.get(1).getRelationToEuro());

    }
}