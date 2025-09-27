package org.aueb.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * This class provides utility methods for processing and comparing date strings.
 * It includes methods to validate and parse date strings and to compare two dates.
 */
public class DateProcessing {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Compares two dates to determine if the first date is before or the same as the second date.
     * @param firstDate The start date in the format "yyyy-MM-dd".
     * @param lastDate The end date in the format "yyyy-MM-dd".
     * @return true if the first date is before or the same as the second date, false otherwise.
     * @throws ParseException If the date strings cannot be parsed into the specified format.
     */
    public static boolean compareDates(String firstDate, String lastDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(firstDate);
        Date date2 = sdf.parse(lastDate);
        if (date1.equals(date2) || date1.before(date2)) {

            return true;

        }
        return false;
    }
    /**
     * Validates and processes a user input string containing one or multiple date ranges.
     * @param listOfDates If true, processes multiple date ranges separated by commas.
     * @return A string containing valid date ranges or null if any date range is invalid.
     */
    public static String checkDates(boolean listOfDates) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String answer = scanner.nextLine();

        if (listOfDates) {
            return processMultipleDates(sdf, answer);
        } else {
            return processSingleDateRange(sdf, answer);
        }
    }
    /**
     * Processes multiple date ranges to ensure they are valid.
     * @param sdf SimpleDateFormat object to use for date parsing.
     * @param input User input containing multiple date ranges.
     * @return The original input if all date ranges are valid; otherwise, null.
     */
    private static String processMultipleDates(SimpleDateFormat sdf, String input) {
        String[] dateRanges = input.split(" , ");
        for (String dateRange : dateRanges) {
            if (!validateDateRange(sdf, dateRange)) {
                System.out.println("The date ranges must be written as yyyy-MM-dd - yyyy-MM-dd.");
                return null;
            }
        }
        return input;
    }

    /**
     * Ensures a single date range is valid and prompts the user repeatedly for correct input if necessary.
     * @param sdf SimpleDateFormat object for date parsing.
     * @param input User input containing a date range.
     * @return A valid date range string.
     */
    private static String processSingleDateRange(SimpleDateFormat sdf, String input) {
        while (true) {
            if (validateDateRange(sdf, input)) {
                return input;
            }
            System.out.println("Enter two dates in the format yyyy-MM-dd - yyyy-MM-dd.");
            input = scanner.nextLine();
        }
    }

    /**
     * Validates whether a given string is a valid date range according to the specified SimpleDateFormat.
     * @param sdf SimpleDateFormat object to use for parsing the dates.
     * @param dateRange A string representing a date range in the format "yyyy-MM-dd - yyyy-MM-dd".
     * @return true if the date range is valid, false otherwise.
     */
    private static boolean validateDateRange(SimpleDateFormat sdf, String dateRange) {
        String[] dates = dateRange.split(" - ");
        if (dates.length != 2) {
            return false;
        }
        try {
            Date startDate = sdf.parse(dates[0]);
            Date endDate = sdf.parse(dates[1]);
            if (!startDate.before(endDate)) {
                System.out.println("The first date must be earlier than the second date.");
                return false;
            }
        } catch (ParseException e) {
            System.out.println("Invalid format. Use yyyy-MM-dd.");
            return false;
        }
        return true;
    }

    /**
     * Subtracts one day from the given date.
     * @param date The date in the format "yyyy-MM-dd".
     * @return The new date, one day earlier, in the format "yyyy-MM-dd".
     * @throws ParseException If the date string cannot be parsed into the specified format.
     */
    public static String subtractOneDay(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = sdf.parse(date);
        parsedDate.setTime(parsedDate.getTime() - (1000 * 60 * 60 * 24)); // subtract one day
        return sdf.format(parsedDate);
    }

}
