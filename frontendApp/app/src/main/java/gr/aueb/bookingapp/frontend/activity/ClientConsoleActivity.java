package gr.aueb.bookingapp.frontend.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import gr.aueb.bookingapp.frontend.view.ClientConsoleView;
import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.viewmodel.ClientConsoleViewModel;

/**
 * Activity for the client console.
 * Implements the ClientConsoleView interface to interact with the ViewModel.
 */
public class ClientConsoleActivity extends AppCompatActivity implements ClientConsoleView {

    private String username;

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel and sets up the UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_console);

        // Get the username from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // Create the ViewModel
        ClientConsoleViewModel viewModel = new ViewModelProvider(this).get(ClientConsoleViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set up buttons for client choices
        Button filter_button = findViewById(R.id.button_filter);
        Button book_button = findViewById(R.id.button_book);
        Button rate_button = findViewById(R.id.button_rate);

        filter_button.setOnClickListener(v -> openFilterPage());
        book_button.setOnClickListener(v -> viewModel.getPresenter().receiveHotels());
        rate_button.setOnClickListener(v -> viewModel.getPresenter().receiveHotelsRate());

        // Set up the log out button
        ImageButton log_out_button = findViewById(R.id.log_out);
        log_out_button.setOnClickListener(v -> viewModel.getPresenter().logOut());

        // Set the welcome message with the username in bold
        TextView welcomeText = findViewById(R.id.textViewClientConsole);
        String welcomeMessage = getString(R.string.welcome) + " " + username + "!";
        SpannableString spannableString = new SpannableString(welcomeMessage);

        int start = welcomeMessage.indexOf(username);
        int end = start + username.length();
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        welcomeText.setText(spannableString);
    }

    /**
     * Opens the ShowHotelsForRateActivity activity.
     */
    public void openRateHotelPage() {
        Intent intent = new Intent(this, ShowHotelsForRateActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Opens the BookHotelActivity.
     */
    public void openBookHotelPage() {
        Intent intent = new Intent(this, BookHotelActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Opens the FilterHotelsActivity.
     */
    public void openFilterPage() {
        Intent intent = new Intent(this, FilterHotelsActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Logs the user out and opens the SignInActivity.
     */
    public void logOut() {
        Intent intent = new Intent(this, SignInActivity.class);
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
