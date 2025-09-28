package gr.aueb.bookingapp.frontend.presenter;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.ReservationsView;

/**
 * Presenter class for handling reservations logic.
 * Interacts with the ReservationsView and ServerAPI to manage reservation data and user interactions.
 */
public class ReservationsPresenter {

    private ReservationsView view;
    private final ServerAPI serverApi;

    /**
     * Constructor to initialize the presenter with the ServerAPI instance.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     */
    public ReservationsPresenter(ServerAPI serverApi) {
        this.serverApi = serverApi;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current ReservationsView instance.
     */
    public ReservationsView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The ReservationsView instance to set.
     */
    public void setView(ReservationsView view) {
        this.view = view;
    }

}
