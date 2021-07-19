package com.example.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CurrencyTimestamp {

    private String currencyCode;

    private Double relationToEuro;

    private LocalDate date;

    public static Double compareTwoValues(CurrencyTimestamp initial, CurrencyTimestamp changed) {
        return (initial.getRelationToEuro()) / (changed.getRelationToEuro()) - 1;
    }

}
