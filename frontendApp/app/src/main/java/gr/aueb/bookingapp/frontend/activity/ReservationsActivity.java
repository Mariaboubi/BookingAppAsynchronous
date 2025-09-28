package gr.aueb.bookingapp.frontend.activity;

import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.ReservationsView;
import gr.aueb.bookingapp.frontend.viewmodel.ReservationsViewModel;

/**
 * Activity for displaying reservations.
 * Implements the ReservationsView interface to interact with the ViewModel.
 */
public class ReservationsActivity extends AppCompatActivity implements ReservationsView {

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel and sets up the UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reservations_item);

        // Initialize the ViewModel
        ReservationsViewModel viewModel = new ViewModelProvider(this).get(ReservationsViewModel.class);
        viewModel.getPresenter().setView(this);
    }

    /**
     * Opens the ShowReservationsActivity.
     */
    public void openShowReservationsActivity() {
        Intent intent = new Intent(this, ShowReservationsActivity.class);
        startActivity(intent);
    }
}
