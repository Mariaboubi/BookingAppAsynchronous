package gr.aueb.bookingapp.frontend.presenter;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.ReservationByAreaResultsView;

/**
 * Presenter class for handling reservation by area results logic.
 * Interacts with the ReservationByAreaResultsView and ServerAPI to manage reservation data and user interactions.
 */
public class ReservationByAreaResultsPresenter {
    private final ServerAPI serverApi;
    private ReservationByAreaResultsView view;

    /**
     * Constructor to initialize the presenter with the ServerAPI instance.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     */
    public ReservationByAreaResultsPresenter(ServerAPI serverApi) {
        this.serverApi = serverApi;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current ReservationByAreaResultsView instance.
     */
    public ReservationByAreaResultsView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The ReservationByAreaResultsView instance to set.
     */
    public void setView(ReservationByAreaResultsView view) {
        this.view = view;
    }
}
