package gr.aueb.bookingapp.frontend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.backend.dao.HotelDAO;
import gr.aueb.bookingapp.backend.memoryDao.HotelDAOMemory;
import gr.aueb.bookingapp.frontend.view.RateHotelView;
import gr.aueb.bookingapp.frontend.viewmodel.RateHotelViewModel;

/**
 * Activity for rating a hotel.
 * Implements the RateHotelView interface to interact with the ViewModel.
 */
public class RateHotelActivity extends AppCompatActivity implements RateHotelView {

    private String HotelName;
    private RateHotelViewModel viewModel;
    private String username;
    private final HotelDAO hotelDAO = new HotelDAOMemory();

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel and sets up the UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_hotel);

        // Retrieve the extras from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            HotelName = extras.getString("HotelName");
            username = extras.getString("username");
        }

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(RateHotelViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set the hotel name
        String str_name = HotelName + " ";
        TextView hotel_name = findViewById(R.id.HotelName);
        hotel_name.setText(str_name);

        // Get the reservations for the hotel and set the text view
        String str_reservations = hotelDAO.findReservationsByHotelName(HotelName) + "";
        str_reservations = str_reservations.replace("[", "").replace("]", "").replace(",", "\n").replace('"', ' ');
        TextView reservations = findViewById(R.id.Reservations);
        reservations.setText(str_reservations);

        // Set up the back button to return to the show hotel page
        ImageButton back_button = findViewById(R.id.Back_button);
        back_button.setOnClickListener(v -> openShowHotelPage());

        // Set up the rate button to submit the rating
        Button rate_button = findViewById(R.id.rateButton);
        rate_button.setOnClickListener(v -> viewModel.getPresenter().attemptRate(this.rate(), HotelName));
    }

    /**
     * Opens the ShowHotelsForRateActivity activity.
     */
    private void openShowHotelPage() {
        Intent intent = new Intent(this, ShowHotelsForRateActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Retrieves the rating entered by the user.
     *
     * @return The rating as a string.
     */
    @Override
    public String rate() {
        return ((EditText) findViewById(R.id.rate)).getText().toString().trim();
    }

    /**
     * Displays an error message as a toast.
     *
     * @param message The error message to display.
     */
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Navigates to the ClientConsoleActivity after rating the hotel.
     */
    public void navigateToActivity() {
        Intent intent = new Intent(this, ClientConsoleActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
