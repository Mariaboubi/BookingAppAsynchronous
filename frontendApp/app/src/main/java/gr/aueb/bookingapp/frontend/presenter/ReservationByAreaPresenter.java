package gr.aueb.bookingapp.frontend.presenter;

import org.json.simple.JSONObject;

import java.text.ParseException;

import gr.aueb.bookingapp.backend.util.DateProcessing;
import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.ReservationByAreaView;

/**
 * Presenter class for handling reservation by area logic.
 * Interacts with the ReservationByAreaView and ServerAPI to manage reservation data and user interactions.
 */
public class ReservationByAreaPresenter {
    private final ServerAPI serverApi;
    private ReservationByAreaView view;

    /**
     * Constructor to initialize the presenter with the ServerAPI instance.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     */
    public ReservationByAreaPresenter(ServerAPI serverApi) {
        this.serverApi = serverApi;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current ReservationByAreaView instance.
     */
    public ReservationByAreaView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The ReservationByAreaView instance to set.
     */
    public void setView(ReservationByAreaView view) {
        this.view = view;
    }

    /**
     * Attempts to set the period for reservations by validating the date and sending a request to the server.
     *
     * @param date The period date in the format "yyyy-MM-dd - yyyy-MM-dd".
     * @throws ParseException If there is an error parsing the dates.
     */
    public void attemptSetPeriod(String date) throws ParseException {
        if (date.isEmpty()) {
            view.showErrorMessage("Date cannot be empty.");
            return;
        }

        if (date.contains(" - ")) {
            String[] dateRanges = date.split(" - ");
            if (!DateProcessing.isValidDate(dateRanges[0]) || !DateProcessing.isValidDate(dateRanges[1])) {
                view.showErrorMessage("Error! The correct format is (yyyy-MM-dd - yyyy-MM-dd)");
                return;
            }

            if (!DateProcessing.compareDates(dateRanges[0], dateRanges[1])) {
                view.showErrorMessage("The first date must be earlier than the second date.");
                return;
            }

            serverApi.reservationByArea(date, new ServerAPI.Callback() {
                @Override
                public void onSuccess(Response response) {
                    JSONObject body = response.getBody();
                    JSONObject results = (JSONObject) body.get("results");
                    view.openReservationsBYAreaResults(results.toString());
                }

                @Override
                public void onFailure(String error) {
                    view.showErrorMessage("Results failed: " + error);
                }
            });
        } else {
            view.showErrorMessage("Wrong date format: (yyyy-MM-dd - yyyy-MM-dd)");
        }
    }
}
