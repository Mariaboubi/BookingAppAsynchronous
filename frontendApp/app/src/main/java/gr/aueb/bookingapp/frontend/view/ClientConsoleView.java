package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles the client console.
 * Provides methods for navigating to different pages and displaying feedback to the user.
 */
public interface ClientConsoleView {

    /**
     * Navigates to the rate hotel page.
     */
    void openRateHotelPage();

    /**
     * Navigates to the book hotel page.
     */
    void openBookHotelPage();

    /**
     * Navigates to the filter hotels page.
     */
    void openFilterPage();

    /**
     * Logs out the user and navigates to the sign-in page.
     */
    void logOut();

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);
}
