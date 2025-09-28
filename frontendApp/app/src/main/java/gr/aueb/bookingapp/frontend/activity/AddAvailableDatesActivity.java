package gr.aueb.bookingapp.frontend.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.widget.Toast;

import java.text.ParseException;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.AddAvailableDatesView;
import gr.aueb.bookingapp.frontend.viewmodel.AddAvailableDatesViewModel;


/**
 * Activity for adding available dates to a hotel.
 * Implements the AddAvailableDatesView interface to interact with the ViewModel.
 */
public class AddAvailableDatesActivity extends AppCompatActivity implements AddAvailableDatesView {
    private AddAvailableDatesViewModel viewModel;
    String username;

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel and sets up the UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_available_dates);

        // Retrieve the username passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(AddAvailableDatesViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set up the back button to return to the manager console
        ImageButton back_button = findViewById(R.id.back_button_add_available_dates);
        back_button.setOnClickListener(v -> viewModel.getPresenter().onManagerConsole());

        // Set up the finish button to add the dates.
        Button finish_button = findViewById(R.id.AddButton);
        finish_button.setOnClickListener(v -> {
            try {
                viewModel.getPresenter().onAddDates(this.extractHotelName(), this.extractDates());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     * Extracts the hotel name from the input field.
     *
     * @return The hotel name as a string.
     */
    @Override
    public String extractHotelName() {
        return ((EditText) findViewById(R.id.name_hotel)).getText().toString().trim();
    }

    /**
     * Extracts the available dates from the input field.
     *
     * @return The available dates as a string.
     */
    @Override
    public String extractDates() {
        return ((EditText) findViewById(R.id.available_dates)).getText().toString().trim();
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
     */
    @Override
    public void openManagerConsoleActivity() {
        Intent intent = new Intent(this, ManagerConsoleActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

}
