package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles showing reservations.
 * Provides methods for navigating to different pages, displaying hotels, and showing error messages.
 */
public interface ShowReservationsView extends View {

    /**
     * Navigates to the manager console activity.
     */
    void openManagerConsoleActivity();

    /**
     * Displays a message indicating that there are no hotels available.
     */
    void ShowNoHotels();

    /**
     * Displays the list of hotels.
     */
    void ShowHotels();

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);
}
