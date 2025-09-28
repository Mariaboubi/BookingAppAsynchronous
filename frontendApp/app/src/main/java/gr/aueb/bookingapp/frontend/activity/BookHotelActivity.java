package gr.aueb.bookingapp.frontend.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import gr.aueb.bookingapp.backend.entities.Hotel;
import gr.aueb.bookingapp.frontend.viewadaptor.BookHotelRecyclerViewAdaptor;
import gr.aueb.bookingapp.frontend.view.BookHotelView;
import gr.aueb.bookingapp.frontend.viewmodel.BookHotelViewModel;
import gr.aueb.bookingapp.R;

/**
 * Activity for booking a hotel.
 * Implements the BookHotelView interface to interact with the ViewModel.
 */
public class BookHotelActivity extends AppCompatActivity implements BookHotelView, BookHotelRecyclerViewAdaptor.HotelSelectionListener {

    private BookHotelViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView emptyView;
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
        setContentView(R.layout.activity_book_hotel);

        // Retrieve the username passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(BookHotelViewModel.class);
        viewModel.getPresenter().setView(this);
        viewModel.getPresenter().setHotelList();

        // Initialize RecyclerView and empty view
        recyclerView = findViewById(R.id.ChooseHotelRecyclerView);
        emptyView = findViewById(R.id.NoHotels);
        viewModel.getPresenter().onChangeLayout();

        // Set up the back button to return to the client console
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> openClientConsolePage());
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
     * Handles the selection of a hotel.
     * Shows a message and opens the MakeReservationActivity with the selected hotel details.
     *
     * @param selectedHotel The hotel that was selected.
     */
    public void selectedHotel(Hotel selectedHotel) {
        Intent intent = new Intent(BookHotelActivity.this, MakeReservationActivity.class);
        intent.putExtra("Hotel", selectedHotel.getHotelName());
        intent.putExtra("username", username);
        intent.putExtra("AvailableDates", selectedHotel.getAvailableDatesAsString());
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

    /**
     * Shows the "no hotels available" view.
     */
    @Override
    public void ShowNoHotels() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the list of available hotels.
     * Configures the RecyclerView with a LinearLayoutManager and sets the adapter.
     */
    @Override
    public void ShowHotels() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BookHotelRecyclerViewAdaptor(this, viewModel.getPresenter().getHotelList(), this, R.layout.activity_book_hotel_item));
    }
}
