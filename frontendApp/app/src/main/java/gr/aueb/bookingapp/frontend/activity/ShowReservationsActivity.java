package gr.aueb.bookingapp.frontend.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.backend.entities.Hotel;
import gr.aueb.bookingapp.frontend.viewadaptor.ShowReservationsRecyclerViewAdaptor;
import gr.aueb.bookingapp.frontend.view.ShowReservationsView;
import gr.aueb.bookingapp.frontend.viewmodel.ShowReservationsViewModel;

/**
 * Activity for displaying hotel reservations.
 * Implements the ShowReservationsView interface to interact with the ViewModel.
 */
public class ShowReservationsActivity extends AppCompatActivity implements ShowReservationsView, ShowReservationsRecyclerViewAdaptor.ReservationSelectionListener {

    private ShowReservationsViewModel viewModel;
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
        setContentView(R.layout.activity_show_reservations);

        // Retrieve the username from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(ShowReservationsViewModel.class);
        viewModel.getPresenter().setView(this);
        viewModel.getPresenter().setHotelList();

        // Set up the back button to return to the manager console
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> viewModel.getPresenter().onManagerConsolePage());

        // Initialize RecyclerView and empty view
        recyclerView = findViewById(R.id.ChooseHotelRecyclerView);
        emptyView = findViewById(R.id.NoHotels);
        viewModel.getPresenter().onChangeLayout();
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

    /**
     * Shows the "no hotels available" view.
     */
    @Override
    public void ShowNoHotels() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the list of available hotels with reservations.
     * Configures the RecyclerView with a LinearLayoutManager and sets the adapter.
     */
    @Override
    public void ShowHotels() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ShowReservationsRecyclerViewAdaptor(this, viewModel.getPresenter().getHotelList(), this, R.layout.activity_show_reservations_item));
    }

    /**
     * Handles the selection of a hotel.
     * This method can be customized to perform actions upon selecting a hotel.
     *
     * @param selectedHotel The hotel that was selected.
     */
    @Override
    public void selectedHotel(Hotel selectedHotel) {
        // Implement actions to perform upon selecting a hotel, if needed
    }
}
