package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles making a reservation.
 * Provides methods for extracting input data, navigating to different pages, and displaying feedback to the user.
 */
public interface MakeReservationView extends View {

    /**
     * Retrieves the date entered by the user for the reservation.
     *
     * @return The date as a String.
     */
    String Date();

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);

    /**
     * Navigates to the client console page.
     */
    void navigateToClientConsole();

    /**
     * Navigates to the book hotel page.
     */
    void openBookHotelPage();
}
