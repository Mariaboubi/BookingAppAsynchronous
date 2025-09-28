package gr.aueb.bookingapp.frontend.presenter;

import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.RateHotelView;

/**
 * Presenter class for handling hotel rating logic.
 * Interacts with the RateHotelView and ServerAPI to manage rating data and user interactions.
 */
public class RateHotelPresenter {
    private final ServerAPI serverApi;
    private RateHotelView view;

    /**
     * Constructor to initialize the presenter with the ServerAPI instance.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     */
    public RateHotelPresenter(ServerAPI serverApi) {
        this.serverApi = serverApi;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current RateHotelView instance.
     */
    public RateHotelView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The RateHotelView instance to set.
     */
    public void setView(RateHotelView view) {
        this.view = view;
    }

    /**
     * Attempts to rate a hotel by validating the rating and sending a rate request to the server.
     *
     * @param rate      The rating value as a string.
     * @param hotelName The name of the hotel to rate.
     */
    public void attemptRate(String rate, String hotelName) {
        if (rate.isEmpty()) {
            view.showErrorMessage("Number of stars cannot be empty.");
            return;
        }

        double star;
        try {
            star = Double.parseDouble(rate);
        } catch (NumberFormatException e) {
            view.showErrorMessage("Number of stars must be a valid number.");
            return;
        }

        if (star < 1 || star > 5) {
            view.showErrorMessage("Number of stars must be between 1 and 5.");
            return;
        }

        serverApi.rate(star, hotelName, new ServerAPI.Callback() {
            @Override
            public void onSuccess(Response response) {
                // Assuming the response will indicate if the rating was successful
                view.showErrorMessage(response.getMessage());
                view.navigateToActivity();
            }

            @Override
            public void onFailure(String error) {
                view.showErrorMessage("Rating failed: " + error);
            }
        });
    }
}
