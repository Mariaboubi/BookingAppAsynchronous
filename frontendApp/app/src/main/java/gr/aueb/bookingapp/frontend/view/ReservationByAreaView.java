package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles setting and displaying reservations by area.
 * Provides methods for extracting input data, navigating to different pages, and displaying feedback to the user.
 */
public interface ReservationByAreaView extends View {

    /**
     * Retrieves the period entered by the user for the reservations.
     *
     * @return The period as a String.
     */
    String extractPeriod();

    /**
     * Navigates to the results page for reservations by area with the given result.
     *
     * @param result The result of the reservations by area as a String.
     */
    void openReservationsBYAreaResults(String result);

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);

    /**
     * Navigates to the manager console activity.
     */
    void openManagerConsoleActivity();
}
