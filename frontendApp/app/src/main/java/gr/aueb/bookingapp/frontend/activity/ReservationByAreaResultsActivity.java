package gr.aueb.bookingapp.frontend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.frontend.view.ReservationByAreaResultsView;
import gr.aueb.bookingapp.frontend.viewmodel.ReservationByAreaResultsViewModel;

/**
 * Activity to display reservation results by area.
 * Implements the ReservationByAreaResultsView interface to interact with the ViewModel.
 */
public class ReservationByAreaResultsActivity extends AppCompatActivity implements ReservationByAreaResultsView {

    private String username;
    private String result;

    /**
     * Called when the activity is first created.
     * Initializes the ViewModel and sets up the UI components.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_by_area_results);

        // Retrieve the extras from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            result = extras.getString("result");
        }

        // Initialize the ViewModel
        ReservationByAreaResultsViewModel viewModel = new ViewModelProvider(this).get(ReservationByAreaResultsViewModel.class);
        viewModel.getPresenter().setView(this);

        // Set up the back button to return to the manager console
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> openManagerConsoleActivity());

        // Convert the JSON string to a Map and format it to a string
        Map<String, Integer> resultMap = parseJsonToMap(result);
        String formattedResult = formatMapToString(resultMap);

        // Set the formatted result to the TextView
        TextView results = findViewById(R.id.ReservationByAreaResults);
        results.setText(formattedResult);
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

    /**
     * Parses a JSON string to a Map.
     *
     * @param jsonString The JSON string to parse.
     * @return A map with the parsed data.
     */
    private Map<String, Integer> parseJsonToMap(String jsonString) {
        Map<String, Integer> map = new HashMap<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

            for (Object keyObj : jsonObject.keySet()) {
                String key = (String) keyObj;
                int value = ((Long) Objects.requireNonNull(jsonObject.get(key))).intValue();
                map.put(key, value);
            }
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
            showErrorMessage("Failed to parse result");
        }
        return map;
    }

    /**
     * Formats a map to a string.
     *
     * @param map The map to format.
     * @return A formatted string representation of the map.
     */
    private String formatMapToString(Map<String, Integer> map) {
        StringBuilder formattedResult = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            formattedResult.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return formattedResult.toString();
    }
}
