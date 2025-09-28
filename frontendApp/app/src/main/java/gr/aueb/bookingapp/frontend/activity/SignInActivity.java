package gr.aueb.bookingapp.frontend.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.widget.Toast;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.SignInView;
import gr.aueb.bookingapp.frontend.viewmodel.SignInViewModel;

/**
 * Activity for signing in.
 * Implements the SignInView interface to interact with the ViewModel.
 */
public class SignInActivity extends AppCompatActivity implements SignInView {

    private SignInViewModel viewModel;

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel and sets up the UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set up the login button to attempt login
        Button loginButton = findViewById(R.id.btnSignIn);
        loginButton.setOnClickListener(v -> viewModel.getPresenter().attemptLogin(this.extractUsername(), this.extractPassword()));

        // Set up the signup button to navigate to the signup page
        TextView signup_button = findViewById(R.id.txtSignUp);
        signup_button.setOnClickListener(v -> viewModel.getPresenter().onSignup());
    }

    /**
     * Extracts the username entered by the user.
     *
     * @return The username as a string.
     */
    public String extractUsername() {
        return ((EditText) findViewById(R.id.usernameText)).getText().toString().trim();
    }

    /**
     * Extracts the password entered by the user.
     *
     * @return The password as a string.
     */
    public String extractPassword() {
        return ((EditText) findViewById(R.id.password_text)).getText().toString().trim();
    }

    /**
     * Opens the SignUpActivity.
     */
    public void openSignupActivity() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates to the ManagerConsoleActivity.
     * Passes the username as an extra.
     *
     * @param username The username to pass to the next activity.
     */
    public void navigateToManagerHome(String username) {
        Intent intent = new Intent(SignInActivity.this, ManagerConsoleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Navigates to the ClientConsoleActivity.
     * Passes the username as an extra.
     *
     * @param username The username to pass to the next activity.
     */
    public void navigateToClientHome(String username) {
        Intent intent = new Intent(SignInActivity.this, ClientConsoleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Displays an error message as a toast.
     *
     * @param message The error message to display.
     */
    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
