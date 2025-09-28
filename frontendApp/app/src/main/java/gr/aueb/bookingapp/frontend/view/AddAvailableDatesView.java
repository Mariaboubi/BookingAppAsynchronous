package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles adding available dates for a hotel.
 * Provides methods for extracting input data and displaying feedback to the user.
 */
public interface AddAvailableDatesView extends View {

    /**
     * Retrieves the hotel name entered by the manager in the "Name of the hotel" field.
     *
     * @return The hotel name as a String.
     */
    String extractHotelName();

    /**
     * Retrieves the available dates entered by the manager in the "Dates" field.
     *
     * @return The available dates as a String.
     */
    String extractDates();

    /**
     * Displays an alert-type message with the specified content.
     *
     * @param message The content of the message.
     */
    void showErrorMessage(String message);

    /**
     * Navigates to the manager console activity.
     * Called when the finish_button or back_button for a manager is pressed.
     */
    void openManagerConsoleActivity();
}
