package gr.aueb.bookingapp.frontend.presenter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import gr.aueb.bookingapp.backend.dao.HotelDAO;
import gr.aueb.bookingapp.backend.entities.Hotel;
import gr.aueb.bookingapp.backend.util.ConvertJSONToHotel;
import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.ManagerConsoleView;

/**
 * Presenter class for managing the manager console.
 * Interacts with the ManagerConsoleView, HotelDAO, and ServerAPI to manage hotel data and user interactions.
 */
public class ManagerConsolePresenter {

    private ManagerConsoleView view;
    private final HotelDAO hotelDAO;
    private final ArrayList<Hotel> hotels;
    private final ServerAPI serverApi;

    /**
     * Constructor to initialize the presenter with the HotelDAO and ServerAPI instances.
     *
     * @param hotelDAO  The HotelDAO instance for accessing hotel data.
     * @param serverApi The ServerAPI instance for handling server communication.
     */
    public ManagerConsolePresenter(HotelDAO hotelDAO, ServerAPI serverApi) {
        this.hotelDAO = hotelDAO;
        this.hotels = new ArrayList<>();
        this.serverApi = serverApi;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current ManagerConsoleView instance.
     */
    public ManagerConsoleView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The ManagerConsoleView instance to set.
     */
    public void setView(ManagerConsoleView view) {
        this.view = view;
    }

    /**
     * Navigates to the activity for adding available dates.
     */
    public void onAddDates() {
        view.openAddDatesActivity();
    }

    /**
     * Retrieves the list of reservations and navigates to the activity for showing reservations.
     */
    public void onShowReservations() {
        serverApi.showReservations(new ServerAPI.Callback() {
            @Override
            public void onSuccess(Response response) {
                JSONObject body = response.getBody();
                initializeHotels(body);
                view.openShowReservationsActivity();
            }

            @Override
            public void onFailure(String errorMessage) {
                view.showErrorMessage(errorMessage);
            }
        });
    }

    /**
     * Initializes the hotel list in the DAO with data from the server response.
     *
     * @param body The JSON object containing the hotel data.
     */
    private void initializeHotels(JSONObject body) {
        JSONArray hotelsArray = (JSONArray) body.get("results");
        hotelDAO.deleteAll();
        for (Object hotelObj : hotelsArray) {
            JSONObject hotel = (JSONObject) hotelObj;
            Hotel newHotel = ConvertJSONToHotel.initializeHotel(hotel);
            JSONObject reservations = (JSONObject) hotel.get("reservations");
            hotelDAO.save(newHotel);
            hotelDAO.associateHotelWithShowReservation(newHotel, reservations.toString());
        }
    }

    /**
     * Navigates to the activity for adding a new hotel.
     */
    public void onAddHotel() {
        view.openAddHotelActivity();
    }

    /**
     * Logs out the user by sending a request to the server.
     */
    public void logOut() {
        serverApi.logOut(new ServerAPI.Callback() {
            @Override
            public void onSuccess(Response response) {
                view.logOut();
            }

            @Override
            public void onFailure(String error) {
                view.showErrorMessage(error);
            }
        });
    }

    /**
     * Navigates to the activity for viewing reservations by area.
     */
    public void onReservationsByArea() {
        view.openReservationsByArea();
    }
}
