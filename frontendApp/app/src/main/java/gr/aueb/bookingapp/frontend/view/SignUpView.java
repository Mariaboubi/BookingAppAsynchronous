package gr.aueb.bookingapp.frontend.view;

/**
 * Interface for the view that handles signing up.
 * Provides methods for extracting input data, navigating to different pages, and displaying feedback to the user.
 */
public interface SignUpView extends View {

    /**
     * Retrieves the role selected by the user.
     *
     * @return The role as a String.
     */
    String extractRole();

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
     * Retrieves the name entered by the user in the Name field.
     *
     * @return The name as a String.
     */
    String extractName();

    /**
     * Retrieves the surname entered by the user in the Surname field.
     *
     * @return The surname as a String.
     */
    String extractSurname();

    /**
     * Retrieves the password confirmation entered by the user in the Confirm Password field.
     *
     * @return The confirmed password as a String.
     */
    String extractConfirmPassword();

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

    /**
     * Navigates to the sign-in activity.
     */
    void openSignInActivity();
}
