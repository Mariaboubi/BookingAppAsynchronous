package gr.aueb.bookingapp.frontend.presenter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

import gr.aueb.bookingapp.backend.dao.HotelDAO;
import gr.aueb.bookingapp.backend.entities.Hotel;
import gr.aueb.bookingapp.backend.util.ConvertJSONToHotel;
import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.ClientConsoleView;

/**
 * Presenter class for handling the client console logic.
 * Interacts with the ClientConsoleView, HotelDAO, and ServerAPI to manage hotel data and user interactions.
 */
public class ClientConsolePresenter {
    private ClientConsoleView view;
    private final ServerAPI serverApi;
    private final HotelDAO hotelDAO;
    private ArrayList<Hotel> hotels;

    /**
     * Constructor to initialize the presenter with the ServerAPI and HotelDAO instances.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     * @param hotelDAO  The HotelDAO instance for accessing hotel data.
     */
    public ClientConsolePresenter(ServerAPI serverApi, HotelDAO hotelDAO) {
        this.serverApi = serverApi;
        this.hotelDAO = hotelDAO;
        this.hotels = new ArrayList<>();
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current ClientConsoleView instance.
     */
    public ClientConsoleView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The ClientConsoleView instance to set.
     */
    public void setView(ClientConsoleView view) {
        this.view = view;
    }

    /**
     * Receives the list of hotels from the server and initializes the hotel list.
     */
    public void receiveHotels() {
        serverApi.receiveHotels(new ServerAPI.Callback() {
            @Override
            public void onSuccess(Response response) {
                JSONObject body = response.getBody();
                initializeHotels(body);
                view.openBookHotelPage();
            }

            @Override
            public void onFailure(String errorMessage) {
                view.showErrorMessage(errorMessage);
            }
        });
    }

    /**
     * Receives the list of hotels for rating from the server and saves them in the DAO.
     */
    public void receiveHotelsRate() {
        serverApi.receiveHotelsRate(new ServerAPI.Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                JSONObject body = response.getBody();
                saveHotelsForRate(body);
                view.openRateHotelPage();
            }

            @Override
            public void onFailure(String errorMessage) {
                view.showErrorMessage(errorMessage);
            }
        });
    }

    /**
     * Saves the hotels for rating into the DAO.
     *
     * @param body The JSON body containing the hotel data.
     */
    private void saveHotelsForRate(JSONObject body) {
        hotelDAO.deleteAll();
        if (body != null) {
            JSONArray reservationsArray = (JSONArray) body.get("reservations");
            for (Object obj : reservationsArray) {
                JSONObject reservation = (JSONObject) obj;
                Hotel hotel = ConvertJSONToHotel.initializeHotel(reservation);
                hotelDAO.save(hotel);
                JSONArray reservations = (JSONArray) reservation.get("reservations");
                List<String> reservationDates = new ArrayList<>();
                for (Object date : reservations) {
                    String res_date = (String) date;
                    reservationDates.add(res_date);
                }
                hotelDAO.associateHotelWithReservations(hotel.getHotelName(), reservationDates);
            }
        }
    }

    /**
     * Initializes the hotel list in the DAO with data from the server response.
     *
     * @param hotels The JSON object containing the hotel data.
     */
    private void initializeHotels(JSONObject hotels) {
        JSONArray hotelsArray = (JSONArray) hotels.get("results");
        hotelDAO.deleteAll();
        for (Object hotelObj : hotelsArray) {
            JSONObject hotel = (JSONObject) hotelObj;
            Hotel newHotel = ConvertJSONToHotel.initializeHotel(hotel);
            hotelDAO.save(newHotel);
        }
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
}
