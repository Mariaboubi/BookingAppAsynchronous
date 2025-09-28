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

import java.text.ParseException;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.MakeReservationView;
import gr.aueb.bookingapp.frontend.viewmodel.MakeReservationViewModel;

/**
 * Activity for making a reservation at a hotel.
 * Implements the MakeReservationView interface to interact with the ViewModel.
 */
public class MakeReservationActivity extends AppCompatActivity implements MakeReservationView {

    private String hotelName;
    private String username;
    private String availableDates;
    private MakeReservationViewModel viewModel;

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel and sets up the UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_reservation);

        // Retrieve the extras from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hotelName = extras.getString("Hotel");
            username = extras.getString("username");
            availableDates = extras.getString("AvailableDates");
        }

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(MakeReservationViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set the hotel name
        String str_name = hotelName + " ";
        TextView hotel_name = findViewById(R.id.HotelName);
        hotel_name.setText(str_name);

        // Set the available dates
        TextView available_dates = findViewById(R.id.AvailableDates);
        availableDates = "Available Dates: \n" + availableDates;
        available_dates.setText(availableDates);

        // Set up the back button to return to the book hotel page
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> openBookHotelPage());

        // Set up the book button to attempt making a reservation
        Button bookButton = findViewById(R.id.SameButton);
        bookButton.setOnClickListener(v -> {
            try {
                viewModel.getPresenter().attemptReservation(this.Date(), hotelName);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Opens the BookHotelActivity.
     * Passes the username as an extra.
     */
    public void openBookHotelPage() {
        Intent intent = new Intent(this, BookHotelActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    /**
     * Retrieves the date entered by the user.
     *
     * @return The date as a string.
     */
    @Override
    public String Date() {
        return ((EditText) findViewById(R.id.Date)).getText().toString().trim();
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
     * Navigates to the ClientConsoleActivity after a successful reservation.
     */
    public void navigateToClientConsole() {
        showErrorMessage("Reservation made successfully");
        Intent intent = new Intent(this, ClientConsoleActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
