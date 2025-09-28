package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles signing in.
 * Provides methods for extracting input data, navigating to different pages, and displaying feedback to the user.
 */
public interface SignInView extends View {

    /**
     * Retrieves the username entered by the user in the Username field.
     *
     * @return The username as a String.
     */
    String extractUsername();

    /**
     * Retrieves the password entered by the user in the Password field.
     *
     * @return The password as a String.
     */
    String extractPassword();

    /**
     * Navigates to the sign-up activity.
     * This method is called when the registration button for a customer is pressed.
     */
    void openSignupActivity();

    /**
     * Displays an error message with the specified content.
     *
     * @param message The content of the error message.
     */
    void showErrorMessage(String message);

    /**
     * Navigates to the manager home activity.
     *
     * @param username The username of the logged-in manager.
     */
    void navigateToManagerHome(String username);

    /**
     * Navigates to the client home activity.
     *
     * @param username The username of the logged-in client.
     */
    void navigateToClientHome(String username);
}
