package gr.aueb.bookingapp.frontend.view;

import org.json.simple.JSONArray;

/**
 * Interface for the view that handles filtering hotels.
 * Provides methods for extracting input data, navigating to different pages, and displaying feedback to the user.
 */
public interface FilterHotelsView extends View {

    /**
     * Retrieves the area entered by the user in the "Area" field.
     *
     * @return The area as a String.
     */
    String extractArea();

    /**
     * Retrieves the date entered by the user in the "Date" field.
     *
     * @return The date as a String.
     */
    String extractDate();

    /**
     * Retrieves the number of people entered by the user in the "People" field.
     *
     * @return The number of people as a String.
     */
    String extractPeople();

    /**
     * Retrieves the star rating entered by the user in the "Stars" field.
     *
     * @return The star rating as a String.
     */
    String extractStars();

    /**
     * Retrieves the price entered by the user in the "Price" field.
     *
     * @return The price as a String.
     */
    String extractPrice();

    /**
     * Navigates to the client console page.
     */
    void openClientConsolePage();

    /**
     * Navigates to the hotels page after filtering.
     *
     * @param filter The filtered hotels as a JSONArray.
     */
    void openHotelsAfterFilter(JSONArray filter);

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);
}
