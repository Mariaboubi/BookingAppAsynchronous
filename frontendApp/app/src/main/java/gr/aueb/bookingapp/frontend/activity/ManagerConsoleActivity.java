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

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.ManagerConsoleView;
import gr.aueb.bookingapp.frontend.viewmodel.ManagerConsoleViewModel;

/**
 * Activity for the manager console.
 * Implements the ManagerConsoleView interface to interact with the ViewModel.
 */
public class ManagerConsoleActivity extends AppCompatActivity implements ManagerConsoleView {

    private ManagerConsoleViewModel viewModel;
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
        setContentView(R.layout.activity_manager_console);

        // Get the username from the intent
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        username = bundle.getString("username");

        // Create the ViewModel
        viewModel = new ViewModelProvider(this).get(ManagerConsoleViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set up buttons for manager actions
        Button addHotelButton = findViewById(R.id.btnAddHotel);
        Button addAvailableDatesButton = findViewById(R.id.btnAddDates);
        Button reservationsButton = findViewById(R.id.btnReservations);
        Button resAreaButton = findViewById(R.id.btnResArea);
        ImageButton logOutButton = findViewById(R.id.log_out);

        addHotelButton.setOnClickListener(v -> viewModel.getPresenter().onAddHotel());
        addAvailableDatesButton.setOnClickListener(v -> viewModel.getPresenter().onAddDates());
        reservationsButton.setOnClickListener(v -> viewModel.getPresenter().onShowReservations());
        resAreaButton.setOnClickListener(v -> viewModel.getPresenter().onReservationsByArea());
        logOutButton.setOnClickListener(v -> viewModel.getPresenter().logOut());

        // Set the welcome message with the username in bold
        TextView welcomeText = findViewById(R.id.textViewManagerConsole);
        String welcomeMessage = getString(R.string.welcome) + " " + username + "!";
        SpannableString spannableString = new SpannableString(welcomeMessage);

        int start = welcomeMessage.indexOf(username);
        int end = start + username.length();
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        welcomeText.setText(spannableString);
    }

    /**
     * Opens the AddAvailableDatesActivity.
     */
    @Override
    public void openAddDatesActivity() {
        Intent intent = new Intent(this, AddAvailableDatesActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Opens the ShowReservationsActivity.
     */
    @Override
    public void openShowReservationsActivity() {
        Intent intent = new Intent(this, ShowReservationsActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Opens the AddHotelActivity.
     */
    @Override
    public void openAddHotelActivity() {
        Intent intent = new Intent(this, AddHotelActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Logs the user out and opens the SignInActivity.
     */
    @Override
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

    /**
     * Opens the ReservationsByAreaActivity.
     */
    @Override
    public void openReservationsByArea() {
        Intent intent = new Intent(this, ReservationsByAreaActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
