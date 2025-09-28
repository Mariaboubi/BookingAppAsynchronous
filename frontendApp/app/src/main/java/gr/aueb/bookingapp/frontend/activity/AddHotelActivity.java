package gr.aueb.bookingapp.frontend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.AddHotelView;
import gr.aueb.bookingapp.frontend.viewmodel.AddHotelViewModel;

/**
 * Activity for adding a new hotel.
 * Implements the AddHotelView interface to interact with the ViewModel.
 */
public class AddHotelActivity extends AppCompatActivity implements AddHotelView {

    private AddHotelViewModel viewModel;
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
        setContentView(R.layout.activity_add_hotel);

        // Retrieve the username passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(AddHotelViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set up the back button to return to the manager console
        ImageButton back_button = findViewById(R.id.back_button_add_hotel);
        back_button.setOnClickListener(v -> openManagerConsoleActivity());

        // Set up the add button to add the hotel
        Button add_button = findViewById(R.id.AddButton);
        add_button.setOnClickListener(v -> viewModel.getPresenter().addHotel());
    }

    /**
     * Extracts the hotel area from the input field.
     *
     * @return The hotel area as a string.
     */
    @Override
    public String extractArea() {
        return ((EditText)findViewById(R.id.Area)).getText().toString().trim();
    }

    /**
     * Extracts the hotel name from the input field.
     *
     * @return The hotel name as a string.
     */
    @Override
    public String extractName() {
        return ((EditText)findViewById(R.id.Name)).getText().toString().trim();
    }

    /**
     * Extracts the number of people from the input field.
     *
     * @return The number of people as a string.
     */
    @Override
    public String extractPeople() {
        return ((EditText)findViewById(R.id.People)).getText().toString().trim();
    }

    /**
     * Extracts the hotel image URL from the input field.
     *
     * @return The hotel image URL as a string.
     */
    @Override
    public String extractImage() {
        return ((EditText)findViewById(R.id.Image)).getText().toString().trim();
    }

    /**
     * Extracts the hotel price from the input field.
     *
     * @return The hotel price as a string.
     */
    @Override
    public String extractPrice() {
        return ((EditText)findViewById(R.id.Price)).getText().toString().trim();
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
