package gr.aueb.bookingapp.frontend.presenter;

import java.text.ParseException;

import gr.aueb.bookingapp.backend.util.DateProcessing;
import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.MakeReservationView;

/**
 * Presenter class for handling reservation logic.
 * Interacts with the MakeReservationView and ServerAPI to manage reservation data and user interactions.
 */
public class MakeReservationPresenter {

    private final ServerAPI serverApi;
    private MakeReservationView view;

    /**
     * Constructor to initialize the presenter with the ServerAPI instance.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     */
    public MakeReservationPresenter(ServerAPI serverApi) {
        this.serverApi = serverApi;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current MakeReservationView instance.
     */
    public MakeReservationView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The MakeReservationView instance to set.
     */
    public void setView(MakeReservationView view) {
        this.view = view;
    }

    /**
     * Attempts to make a reservation by validating the date and sending a reservation request to the server.
     *
     * @param date      The reservation date in the format "yyyy-MM-dd - yyyy-MM-dd".
     * @param hotelName The name of the hotel for the reservation.
     * @throws ParseException If there is an error parsing the dates.
     */
    public void attemptReservation(String date, String hotelName) throws ParseException {
        if (date.isEmpty()) {
            view.showErrorMessage("Date cannot be empty");
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

            serverApi.reservation(date, hotelName, new ServerAPI.Callback() {
                @Override
                public void onSuccess(Response response) {
                    // Assuming the response will indicate if the reservation was successful
                    view.navigateToClientConsole();
                }

                @Override
                public void onFailure(String error) {
                    view.showErrorMessage("Reservation failed: " + error);
                }
            });
        } else {
            view.showErrorMessage("Error! The correct format is (yyyy-MM-dd - yyyy-MM-dd)");
        }
    }
}
