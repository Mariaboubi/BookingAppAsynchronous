package gr.aueb.bookingapp.frontend.view;


/**
 * Interface for the view that handles booking hotels.
 * Provides methods for displaying hotels and providing feedback to the user.
 */
public interface BookHotelView {

    /**
     * Displays a message indicating that there are no hotels available.
     */
    void ShowNoHotels();

    /**
     * Displays the list of hotels and sets up the RecyclerView for selection.
     * Hides the message indicating the absence of hotels.
     */
    void ShowHotels();

    /**
     * Navigates to the client console page.
     */
    void openClientConsolePage();

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);
}

