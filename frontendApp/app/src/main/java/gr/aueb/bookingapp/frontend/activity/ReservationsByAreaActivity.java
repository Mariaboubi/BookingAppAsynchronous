package gr.aueb.bookingapp.frontend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.ParseException;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.ReservationByAreaView;
import gr.aueb.bookingapp.frontend.viewmodel.ReservationByAreaViewModel;

/**
 * Activity for viewing reservations by area.
 * Implements the ReservationByAreaView interface to interact with the ViewModel.
 */
public class ReservationsByAreaActivity extends AppCompatActivity implements ReservationByAreaView {
    private String username;
    private ReservationByAreaViewModel viewModel;

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel and sets up the UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_by_area);

        // Retrieve the username from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(ReservationByAreaViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set up the back button to return to the manager console
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> openManagerConsoleActivity());

        // Set up the confirm button to submit the period for reservations by area
        Button confirmButton = findViewById(R.id.Confirm);
        confirmButton.setOnClickListener(v -> {
            try {
                viewModel.getPresenter().attemptSetPeriod(this.extractPeriod());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Retrieves the period entered by the user.
     *
     * @return The period as a string.
     */
    @Override
    public String extractPeriod() {
        return ((EditText) findViewById(R.id.Period)).getText().toString().trim();
    }

    /**
     * Opens the ReservationByAreaResultsActivity activity.
     * Passes the username and result as extras.
     *
     * @param result The result string to pass to the next activity.
     */
    @Override
    public void openReservationsBYAreaResults(String result) {
        Intent intent = new Intent(this, ReservationByAreaResultsActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("result", result);
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
     * Opens the ManagerConsoleActivity.
     * Passes the username as an extra.
     */
    @Override
    public void openManagerConsoleActivity() {
        Intent intent = new Intent(this, ManagerConsoleActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
