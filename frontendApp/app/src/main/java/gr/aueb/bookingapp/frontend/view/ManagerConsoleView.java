package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles the manager console.
 * Provides methods for navigating to different pages and displaying feedback to the user.
 */
public interface ManagerConsoleView extends View {

    /**
     * Navigates to the add available dates activity.
     * This method is called when the add_dates button for a manager is pressed.
     */
    void openAddDatesActivity();

    /**
     * Navigates to the show reservations activity.
     * This method is called when the show_reservations button for a manager is pressed.
     */
    void openShowReservationsActivity();

    /**
     * Navigates to the add hotel activity.
     * This method is called when the add_hotel button for a manager is pressed.
     */
    void openAddHotelActivity();

    /**
     * Logs out the user and navigates to the sign-in page.
     */
    void logOut();

    /**
     * Navigates to the reservations by area activity.
     * This method is called when the reservations_by_area button for a manager is pressed.
     */
    void openReservationsByArea();

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);
}
