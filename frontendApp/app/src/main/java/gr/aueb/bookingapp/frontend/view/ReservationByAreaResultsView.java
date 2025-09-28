package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles displaying reservation results by area.
 * Provides methods for displaying error messages and navigating to different pages.
 */
public interface ReservationByAreaResultsView extends View {

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
