package gr.aueb.bookingapp.frontend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.SignUpView;
import gr.aueb.bookingapp.frontend.viewmodel.SignUpViewModel;

/**
 * Activity for signing up a new user.
 * Implements the SignUpView interface to interact with the ViewModel.
 */
public class SignUpActivity extends AppCompatActivity implements SignUpView {

    private SignUpViewModel viewModel;
    private RadioGroup toggleGroup;

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel and sets up the UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.getPresenter().setView(this);

        toggleGroup = findViewById(R.id.toggle);

        // Set up the sign-up button to attempt sign-up
        Button signUpButton = findViewById(R.id.btnSignUp);
        signUpButton.setOnClickListener(v -> viewModel.getPresenter().attemptSignUp(
                this.extractName(),
                this.extractSurname(),
                this.extractUsername(),
                this.extractPassword(),
                this.extractConfirmPassword(),
                this.extractRole()
        ));

        // Set up the sign-in button to navigate to the sign-in page
        TextView signInButton = findViewById(R.id.txtSignIn);
        signInButton.setOnClickListener(v -> viewModel.getPresenter().onSignIn());
    }

    /**
     * Extracts the username entered by the user.
     *
     * @return The username as a string.
     */
    @Override
    public String extractUsername() {
        return ((EditText) findViewById(R.id.usernameText)).getText().toString().trim();
    }

    /**
     * Extracts the password entered by the user.
     *
     * @return The password as a string.
     */
    @Override
    public String extractPassword() {
        return ((EditText) findViewById(R.id.password_text)).getText().toString().trim();
    }

    /**
     * Extracts the first name entered by the user.
     *
     * @return The first name as a string.
     */
    @Override
    public String extractName() {
        return ((EditText) findViewById(R.id.nameText)).getText().toString().trim();
    }

    /**
     * Extracts the surname entered by the user.
     *
     * @return The surname as a string.
     */
    @Override
    public String extractSurname() {
        return ((EditText) findViewById(R.id.surnameText)).getText().toString().trim();
    }

    /**
     * Extracts the confirm password entered by the user.
     *
     * @return The confirm password as a string.
     */
    @Override
    public String extractConfirmPassword() {
        return ((EditText) findViewById(R.id.SignUpConfirmPassword)).getText().toString().trim();
    }

    /**
     * Extracts the selected role (Manager or Client) from the radio buttons.
     *
     * @return The selected role as a string.
     */
    @Override
    public String extractRole() {
        int selectedId = toggleGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.manager_choice) {
            return "Manager";
        } else if (selectedId == R.id.client_choice) {
            return "Client";
        } else {
            return "";
        }
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

    /**
     * Navigates to the ManagerConsoleActivity.
     * Passes the username as an extra.
     *
     * @param username The username to pass to the next activity.
     */
    @Override
    public void navigateToManagerHome(String username) {
        Intent intent = new Intent(SignUpActivity.this, ManagerConsoleActivity.class);
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
    @Override
    public void navigateToClientHome(String username) {
        Intent intent = new Intent(SignUpActivity.this, ClientConsoleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Opens the SignInActivity.
     */
    @Override
    public void openSignInActivity() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
    }
}
