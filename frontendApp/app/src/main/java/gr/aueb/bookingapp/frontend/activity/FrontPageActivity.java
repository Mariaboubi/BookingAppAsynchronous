package gr.aueb.bookingapp.frontend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import gr.aueb.bookingapp.R;

/**
 * Activity for the front page (splash screen) of the app.
 * Displays the front page for a set duration before navigating to the sign-in activity.
 */
public class FrontPageActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Sets up the front page view and initializes the handler to navigate to the sign-in activity after a delay.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        // Initialize the handler
        Handler handler = new Handler();

        // Set a delay to transition from the front page to the sign-in activity
        handler.postDelayed(() -> {
            Intent intent = new Intent(FrontPageActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }, 2000); // 2-second delay
    }
}
