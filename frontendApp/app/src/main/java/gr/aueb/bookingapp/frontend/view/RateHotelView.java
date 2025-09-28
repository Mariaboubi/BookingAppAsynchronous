package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles rating a hotel.
 * Provides methods for extracting input data, navigating to different pages, and displaying feedback to the user.
 */
public interface RateHotelView extends View {

    /**
     * Retrieves the rating entered by the user for the hotel.
     *
     * @return The rating as a String.
     */
    String rate();

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);

    /**
     * Navigates to a specific activity after rating the hotel.
     */
    void navigateToActivity();
}
