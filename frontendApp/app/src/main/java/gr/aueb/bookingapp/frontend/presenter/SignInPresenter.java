package gr.aueb.bookingapp.frontend.presenter;

import android.content.Context;
import android.widget.Toast;

import org.json.simple.JSONObject;

import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.SignInView;

/**
 * Presenter class for handling sign-in logic.
 * Interacts with the SignInView and ServerAPI to manage login data and user interactions.
 */
public class SignInPresenter {
    private SignInView view;
    private final ServerAPI serverApi;

    /**
     * Constructor to initialize the presenter with the ServerAPI instance.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     */
    public SignInPresenter(ServerAPI serverApi) {
        this.serverApi = serverApi;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current SignInView instance.
     */
    public SignInView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The SignInView instance to set.
     */
    public void setView(SignInView view) {
        this.view = view;
    }

    /**
     * Attempts to log in the user by validating the input and sending a login request to the server.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     */
    public void attemptLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            view.showErrorMessage("Username and password cannot be empty");
            return;
        }

        // Using the asynchronous login method
        serverApi.login(username, password, new ServerAPI.Callback() {
            @Override
            public void onSuccess(Response response) {
                JSONObject body = response.getBody();
                String userRole = (String) body.get("user_role");

                if (userRole != null) {
                    if (userRole.equalsIgnoreCase("Manager")) {
                        view.navigateToManagerHome(username);
                    } else if (userRole.equalsIgnoreCase("Client")) {
                        view.navigateToClientHome(username);
                    }
                    Toast.makeText((Context) view, "Login successful", Toast.LENGTH_LONG).show();
                } else {
                    view.showErrorMessage("Login failed: Invalid role");
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText((Context) view, "Login failed", Toast.LENGTH_LONG).show();
                view.showErrorMessage("Authentication failed: " + error);
            }
        });
    }

    /**
     * Navigates to the signup activity.
     */
    public void onSignup() {
        view.openSignupActivity();
    }
}
