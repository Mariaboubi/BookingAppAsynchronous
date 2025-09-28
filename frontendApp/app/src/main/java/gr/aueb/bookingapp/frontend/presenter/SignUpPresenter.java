package gr.aueb.bookingapp.frontend.presenter;

import android.content.Context;
import android.widget.Toast;

import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.SignUpView;

/**
 * Presenter class for handling sign-up logic.
 * Interacts with the SignUpView and ServerAPI to manage sign-up data and user interactions.
 */
public class SignUpPresenter {

    private SignUpView view;
    private final ServerAPI serverAPI;

    /**
     * Constructor to initialize the presenter with the ServerAPI instance.
     *
     * @param serverAPI The ServerAPI instance for handling server communication.
     */
    public SignUpPresenter(ServerAPI serverAPI) {
        this.serverAPI = serverAPI;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current SignUpView instance.
     */
    public SignUpView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The SignUpView instance to set.
     */
    public void setView(SignUpView view) {
        this.view = view;
    }

    /**
     * Attempts to sign up the user by validating the input and sending a registration request to the server.
     *
     * @param name            The name entered by the user.
     * @param surname         The surname entered by the user.
     * @param username        The username entered by the user.
     * @param password        The password entered by the user.
     * @param confirmPassword The confirmation of the password entered by the user.
     * @param role            The role selected by the user (e.g., Manager, Client).
     */
    public void attemptSignUp(String name, String surname, String username, String password, String confirmPassword, String role) {
        if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.showErrorMessage("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.showErrorMessage("Passwords do not match.");
            return;
        }

        if (role.isEmpty()) {
            view.showErrorMessage("Please select a role.");
            return;
        }

        serverAPI.registerUser(name, surname, username, password, role, new ServerAPI.Callback() {
            @Override
            public void onSuccess(Response response) {
                if (role.equals("Manager")) {
                    view.navigateToManagerHome(username);
                } else {
                    view.navigateToClientHome(username);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText((Context) view, "Register failed", Toast.LENGTH_LONG).show();
                view.showErrorMessage(errorMessage);
            }
        });
    }

    /**
     * Navigates to the sign-in activity.
     */
    public void onSignIn() {
        view.openSignInActivity();
    }
}
