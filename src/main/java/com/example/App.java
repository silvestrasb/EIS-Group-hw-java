package com.example;

import com.example.entity.CurrencyTimestamp;
import com.example.http_request.LbHttpRequest;
import com.example.util.CsvDeserializer;
import com.example.util.PrettyOutput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class App {

    public static final String SAVE_DIRECTORY = System.getProperty("user.dir").concat("\\currency_watcher");
    private static final DateTimeFormatter dateFormatter = LbHttpRequest.DATE_FORMATTER;
    private static final String dateFormatterString = "yyyy-MM-dd";
    private static final String FILE_NAME_FORMAT = "%s_from_%s_to_%s.csv";
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Getting all available currency codes from the server...");
        String csvCurrencyCodesString = null;
        try {
            csvCurrencyCodesString = LbHttpRequest.getCurrencyCodes().replace("\"", "");
        } catch (IOException | InterruptedException e) {
            System.out.println("ERROR: failed to get currency codes from server.");
            System.exit(-1);
        }

        CsvDeserializer deserializer = new CsvDeserializer(";");
        List<String> currencyCodeList = null;

        try {
            currencyCodeList = deserializer.csvToCurrencyCodeList(csvCurrencyCodesString, 1);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to deserialize currency code list.");
            System.exit(-1);
        }

        PrettyOutput.beautifyPrint("currency watcher");
        PrettyOutput.beautifyPrint("currencies in relation to euro");

        boolean mainLoopStatus = true;
        while (mainLoopStatus) {
            System.out.print("Enter currency code: ");
            String currencyCode = in.nextLine().toUpperCase(Locale.ROOT);

            if (!currencyCodeList.contains(currencyCode)) {
                if (currencyCodeList.size() > 0) {
                    System.out.println("Wrong currency code, try again.");
                    System.out.println("[l] - list all currency codes.");
                    if (in.nextLine().equals("l")) {
                        currencyCodeList.forEach(System.out::println);
                    }
                    continue;
                }
            }

            System.out.println("Enter time period (" + dateFormatterString + "):");
            System.out.print("From: ");
            LocalDate fromDate = getValidDate();
            System.out.print("To: ");
            LocalDate toDate = getValidDate();

            String csvCurrencyTimestampString = null;
            try {
                System.out.println("Getting currency timestamps from the server...");
                csvCurrencyTimestampString = LbHttpRequest.getCsvCurrencyTimestamps(currencyCode, fromDate, toDate);
            } catch (IOException | InterruptedException e) {
                System.out.println("ERROR: failed to get currency information from the server.");
                System.exit(-1);
            }


            csvCurrencyTimestampString = csvCurrencyTimestampString.replace("\"", "");
            List<CurrencyTimestamp> currencyTimestampList = null;

            try {
                currencyTimestampList = deserializer.csvToCurrencyTimestamp(csvCurrencyTimestampString,
                        1, 2, 3, dateFormatter);
            } catch (IOException e) {
                System.out.println("ERROR: Failed to deserialize from csv to currency timestamp objects.");
                System.exit(-1);
            }

            printCurrencyTimestampResults(currencyTimestampList, currencyCode, fromDate, toDate);

            boolean switchLoop = true;
            while (switchLoop) {
                printEndMenu();
                String choice = in.nextLine();
                switch (choice.toUpperCase(Locale.ROOT)) {
                    case "S":
                        saveCsvFile(csvCurrencyTimestampString, currencyCode, fromDate, toDate);
                        break;
                    case "R":
                        switchLoop = false;
                        break;
                    case "X":
                        System.exit(-1);
                        break;
                    default:
                        System.out.println("Invalid command, try again.");
                        break;
                }
            }
        }
    }

    public static LocalDate getValidDate() {
        LocalDate localDate;
        while (true) {
            try {
                localDate = LocalDate.parse(in.nextLine(), dateFormatter);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("ERROR: Wrong date format");
                System.out.println("Expected date format: " + dateFormatterString);
                System.out.println("Try Again:");
            }
        }
        return localDate;
    }

    public static void printEndMenu() {
        System.out.println("[s] - save the results");
        System.out.println("[r] - run another scan");
        System.out.println("[x] - exit the application");
    }

    public static void printCurrencyTimestampResults(List<CurrencyTimestamp> currencyTimestampList,
                                                     String currencyCode, LocalDate fromDate, LocalDate toDate) {

        PrettyOutput.beautifyPrint("Results");
        PrettyOutput.beautifyPrint(String.format("%s from %s to %s", currencyCode, fromDate, toDate));

        if (!(currencyTimestampList.size() > 0)) {
            return;
        }
        Collections.sort(currencyTimestampList, Comparator.comparing(CurrencyTimestamp::getDate));

        CurrencyTimestamp earliest = currencyTimestampList.get(0);
        CurrencyTimestamp latest = currencyTimestampList.get(currencyTimestampList.size() - 1);

        Double change = CurrencyTimestamp.compareTwoValues(earliest, latest);

        currencyTimestampList
                .forEach(o -> System.out.println(o.getCurrencyCode() + " " + o.getDate() + " : " + o.getRelationToEuro()));

        System.out.println("Change: " + change + " %");
        PrettyOutput.beautifyPrint("");
    }

    public static void saveCsvFile(String csvString, String currencyCode, LocalDate fromDate, LocalDate toDate) {

        String fileName = String.format(FILE_NAME_FORMAT, currencyCode, fromDate, toDate);
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(SAVE_DIRECTORY + "/" + fileName);
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(csvString);
            bw.close();
            System.out.println("Successfully wrote to a file.");
        } catch (IOException e) {
            System.out.println("ERROR: Could not write to a file.");
        }
    }

}
