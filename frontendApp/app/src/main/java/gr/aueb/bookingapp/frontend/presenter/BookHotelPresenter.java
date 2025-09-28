package gr.aueb.bookingapp.frontend.presenter;

import java.util.ArrayList;

import gr.aueb.bookingapp.backend.entities.Hotel;
import gr.aueb.bookingapp.frontend.view.BookHotelView;
import gr.aueb.bookingapp.backend.dao.HotelDAO;

/**
 * Presenter class for handling hotel booking logic.
 * Interacts with the BookHotelView and HotelDAO to manage hotel data and user interactions.
 */
public class BookHotelPresenter {

    private BookHotelView view;
    private final HotelDAO hotelDAO;
    private ArrayList<Hotel> hotels;

    /**
     * Constructor to initialize the presenter with the HotelDAO instance.
     *
     * @param hotelDAO The HotelDAO instance for accessing hotel data.
     */
    public BookHotelPresenter(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
        this.hotels = new ArrayList<>();
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The BookHotelView instance to set.
     */
    public void setView(BookHotelView view) {
        this.view = view;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current BookHotelView instance.
     */
    public BookHotelView getView() {
        return view;
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
