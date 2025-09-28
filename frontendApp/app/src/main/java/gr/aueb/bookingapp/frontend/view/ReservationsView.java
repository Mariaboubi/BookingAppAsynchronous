package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles displaying reservations.
 * Provides a method for navigating to the show reservations activity.
 */
public interface ReservationsView extends View {

    /**
     * Navigates to the show reservations activity.
     * This method is called when the back button for a manager is pressed.
     */
    void openShowReservationsActivity();
}
