package gr.aueb.bookingapp.frontend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.json.simple.JSONArray;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.FilterHotelsView;
import gr.aueb.bookingapp.frontend.viewmodel.FilterHotelViewModel;

/**
 * Activity for filtering hotels.
 * Implements the FilterHotelsView interface to interact with the ViewModel.
 */
public class FilterHotelsActivity extends AppCompatActivity implements FilterHotelsView {

    private FilterHotelViewModel viewModel;
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
        setContentView(R.layout.activity_filter_hotels);

        // Retrieve the username from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(FilterHotelViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set up the back button to return to the client console
        ImageButton back_button = findViewById(R.id.back_button_filter_hotels);
        back_button.setOnClickListener(v -> openClientConsolePage());

        // Set up the search button to filter hotels
        Button search_button = findViewById(R.id.SearchButton);
        search_button.setOnClickListener(v -> viewModel.getPresenter().chooseFilter());
    }

    /**
     * Extracts the area from the input field.
     *
     * @return The area as a string.
     */
    @Override
    public String extractArea() {
        return ((EditText)findViewById(R.id.Area)).getText().toString().trim();
    }

    /**
     * Extracts the date from the input field.
     *
     * @return The date as a string.
     */
    @Override
    public String extractDate() {
        return ((EditText)findViewById(R.id.Date)).getText().toString().trim();
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
     * Extracts the star rating from the input field.
     *
     * @return The star rating as a string.
     */
    @Override
    public String extractStars() {
        return ((EditText)findViewById(R.id.Star)).getText().toString().trim();
    }

    /**
     * Extracts the price from the input field.
     *
     * @return The price as a string.
     */
    @Override
    public String extractPrice() {
        return ((EditText)findViewById(R.id.Price)).getText().toString().trim();
    }

    /**
     * Opens the ClientConsoleActivity.
     * Passes the username as an extra.
     */
    public void openClientConsolePage() {
        Intent intent = new Intent(this, ClientConsoleActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Opens the BookHotelActivity after successfully filtering hotels.
     * Displays a success message.
     *
     * @param hotels The filtered hotels as a JSONArray.
     */
    public void openHotelsAfterFilter(JSONArray hotels) {
        Intent intent = new Intent(this, BookHotelActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Displays an error message as a toast.
     *
     * @param message The error message to display.
     */
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
