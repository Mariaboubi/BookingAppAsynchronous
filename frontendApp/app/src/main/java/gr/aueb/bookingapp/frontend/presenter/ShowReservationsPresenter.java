package gr.aueb.bookingapp.frontend.presenter;

import java.util.ArrayList;

import gr.aueb.bookingapp.backend.dao.HotelDAO;
import gr.aueb.bookingapp.backend.entities.Hotel;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.ShowReservationsView;

/**
 * Presenter class for handling the display of reservations.
 * Interacts with the ShowReservationsView and HotelDAO to manage reservation data and user interactions.
 */
public class ShowReservationsPresenter {

    private ShowReservationsView view;
    private final ServerAPI serverApi;
    private final HotelDAO hotelDAO;
    private ArrayList<Hotel> hotels;

    /**
     * Constructor to initialize the presenter with the ServerAPI and HotelDAO instances.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     * @param hotelDAO  The HotelDAO instance for accessing hotel data.
     */
    public ShowReservationsPresenter(ServerAPI serverApi, HotelDAO hotelDAO) {
        this.serverApi = serverApi;
        this.hotelDAO = hotelDAO;
        this.hotels = new ArrayList<>();
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current ShowReservationsView instance.
     */
    public ShowReservationsView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The ShowReservationsView instance to set.
     */
    public void setView(ShowReservationsView view) {
        this.view = view;
    }

    /**
     * Navigates back to the manager console activity.
     */
    public void onManagerConsolePage() {
        view.openManagerConsoleActivity();
    }

    /**
     * Retrieves the list of all hotels from the DAO and stores it in the hotels list.
     */
    public void setHotelList() {
        this.hotels = hotelDAO.findAll();
    }

    /**
     * Returns the list of hotels.
     *
     * @return The list of hotels.
     */
    public ArrayList<Hotel> getHotelList() {
        return hotels;
    }

    /**
     * Updates the layout based on whether the hotel list is empty or not.
     * Calls the appropriate view method to show hotels or indicate no hotels available.
     */
    public void onChangeLayout() {
        if (hotels.isEmpty()) {
            view.ShowNoHotels();
        } else {
            view.ShowHotels();
        }
    }
}
