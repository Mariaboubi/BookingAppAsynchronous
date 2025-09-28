package gr.aueb.bookingapp.frontend.presenter;

import org.json.simple.JSONObject;

import java.text.ParseException;

import gr.aueb.bookingapp.backend.util.DateProcessing;
import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.AddAvailableDatesView;

/**
 * Presenter class for handling the addition of available dates for a hotel.
 * Interacts with the AddAvailableDatesView and ServerAPI to process user input and server responses.
 */
public class AddAvailableDatesPresenter {

    private AddAvailableDatesView view;
    private final ServerAPI serverApi;

    /**
     * Constructor to initialize the presenter with the ServerAPI instance.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     */
    public AddAvailableDatesPresenter(ServerAPI serverApi) {
        this.serverApi = serverApi;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current AddAvailableDatesView instance.
     */
    public AddAvailableDatesView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The AddAvailableDatesView instance to set.
     */
    public void setView(AddAvailableDatesView view) {
        this.view = view;
    }

    /**
     * Navigates back to the manager console activity.
     */
    public void onManagerConsole() {
        view.openManagerConsoleActivity();
    }

    /**
     * Processes the addition of available dates for a hotel.
     *
     * @param hotelName The name of the hotel.
     * @param dates     The available dates to add in the format "yyyy-MM-dd - yyyy-MM-dd".
     * @throws ParseException If there is an error parsing the dates.
     */
    public void onAddDates(String hotelName, String dates) throws ParseException {
        if (hotelName.isEmpty() || dates.isEmpty()) {
            view.showErrorMessage("Please fill in all fields.");
            return;
        }

        if (dates.contains(" - ")) {
            String[] dateRanges = dates.split(" - ");

            // Check date validity
            if (!DateProcessing.isValidDate(dateRanges[0]) || !DateProcessing.isValidDate(dateRanges[1])) {
                view.showErrorMessage("Correct format: (yyyy-MM-dd - yyyy-MM-dd)");
                return;
            }

            // Check if the first date is earlier than the second date
            if (!DateProcessing.compareDates(dateRanges[0], dateRanges[1])) {
                view.showErrorMessage("The first date must be earlier than the second date.");
                return;
            }

            // Create JSON object for the request
            JSONObject addDates = new JSONObject();
            addDates.put("type", "2");
            addDates.put("hotelName", hotelName);
            addDates.put("availableDates", dates);

            // Send the request to the server
            serverApi.addAvailableDates(addDates, new ServerAPI.Callback() {
                @Override
                public void onSuccess(Response response) {
                    view.showErrorMessage(response.getMessage());
                    view.openManagerConsoleActivity();
                }

                @Override
                public void onFailure(String error) {
                    view.showErrorMessage(error);
                }
            });
        } else {
            view.showErrorMessage("Correct format: (yyyy-MM-dd - yyyy-MM-dd)");
        }
    }
}
