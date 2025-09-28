package gr.aueb.bookingapp.backend.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class provides utility methods for processing and comparing date strings.
 * It includes methods to validate and parse date strings and to compare two dates.
 */
public class DateProcessing {

    /**
     * Compares two date strings to determine if the start date is before or equal to the end date.
     *
     * @param startDate The start date string in the format "yyyy-MM-dd".
     * @param endDate The end date string in the format "yyyy-MM-dd".
     * @return true if the start date is before or equal to the end date, false otherwise.
     */
    public static boolean compareDates(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return start.before(end) || start.equals(end);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Validates if a date string is in the correct format and represents a valid date.
     *
     * @param date The date string in the format "yyyy-MM-dd".
     * @return true if the date string is valid, false otherwise.
     */
    public static boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            Date parsedDate = sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
