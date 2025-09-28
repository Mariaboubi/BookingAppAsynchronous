package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles adding a new hotel.
 * Provides methods for extracting input data and displaying feedback to the user.
 */
public interface AddHotelView extends View {

    /**
     * Retrieves the area entered by the user in the "Area" field.
     *
     * @return The area as a String.
     */
    String extractArea();

    /**
     * Retrieves the hotel name entered by the user in the "Name" field.
     *
     * @return The hotel name as a String.
     */
    String extractName();

    /**
     * Retrieves the number of people entered by the user in the "People" field.
     *
     * @return The number of people as a String.
     */
    String extractPeople();

    /**
     * Retrieves the image URL or path entered by the user in the "Image" field.
     *
     * @return The image URL or path as a String.
     */
    String extractImage();

    /**
     * Retrieves the price entered by the user in the "Price" field.
     *
     * @return The price as a String.
     */
    String extractPrice();

    /**
     * Navigates to the manager console activity.
     */
    void openManagerConsoleActivity();

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);
}
