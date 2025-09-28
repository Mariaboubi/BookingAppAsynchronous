package gr.aueb.bookingapp.frontend.presenter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import gr.aueb.bookingapp.backend.entities.Hotel;
import gr.aueb.bookingapp.backend.dao.HotelDAO;
import gr.aueb.bookingapp.backend.util.ConvertJSONToHotel;
import gr.aueb.bookingapp.backend.util.DateProcessing;
import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.FilterHotelsView;

/**
 * Presenter class for handling hotel filtering logic.
 * Interacts with the FilterHotelsView, HotelDAO, and ServerAPI to manage hotel data and user interactions.
 */
public class FilterHotelPresenter {
    private FilterHotelsView view;
    private final ServerAPI serverApi;
    private final HotelDAO hotelDAO;
    private ArrayList<Hotel> hotels;

    /**
     * Constructor to initialize the presenter with the ServerAPI and HotelDAO instances.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     * @param hotelDAO  The HotelDAO instance for accessing hotel data.
     */
    public FilterHotelPresenter(ServerAPI serverApi, HotelDAO hotelDAO) {
        this.serverApi = serverApi;
        this.hotelDAO = hotelDAO;
        this.hotels = new ArrayList<>();
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current FilterHotelsView instance.
     */
    public FilterHotelsView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The FilterHotelsView instance to set.
     */
    public void setView(FilterHotelsView view) {
        this.view = view;
    }

    /**
     * Processes the filter criteria entered by the user, validates them, and sends a filter request to the server.
     */
    public void chooseFilter() {
        String area = view.extractArea();
        String date = view.extractDate();
        String numOfPeople = view.extractPeople();
        String price = view.extractPrice();
        String stars = view.extractStars();

        JSONObject filter = new JSONObject();
        JSONArray selectedFilters = new JSONArray();

        if (!area.isEmpty()) {
            if (area.length() < 3) {
                view.showErrorMessage("Area must have at least 3 characters.");
                return;
            } else {
                selectedFilters.add("1");
                filter.put("area", area);
            }
        }

        if (!date.isEmpty()) {
            if (!DateProcessing.isValidDate(date)) {
                view.showErrorMessage("Correct date format is: yyyy-MM-dd");
                return;
            } else {
                selectedFilters.add("2");
                filter.put("date", date);
            }
        }

        if (!numOfPeople.isEmpty()) {
            try {
                int people = Integer.parseInt(numOfPeople);
                if (people < 1) {
                    view.showErrorMessage("Number of people field must be an integer and a positive number");
                    return;
                } else {
                    selectedFilters.add("3");
                    filter.put("numPeople", people);
                }
            } catch (NumberFormatException e) {
                view.showErrorMessage("Number of people must be a valid integer");
                return;
            }
        }

        if (!price.isEmpty()) {
            try {
                double priceValue = Double.parseDouble(price);
                if (priceValue < 1.0) {
                    view.showErrorMessage("Price field must be a positive number");
                    return;
                } else {
                    selectedFilters.add("4");
                    filter.put("price", priceValue);
                }
            } catch (NumberFormatException e) {
                view.showErrorMessage("Price must be a valid number");
                return;
            }
        }

        if (!stars.isEmpty()) {
            try {
                double starValue = Double.parseDouble(stars);
                if (starValue < 1 || starValue > 5) {
                    view.showErrorMessage("Number of stars must be between 1 and 5");
                    return;
                } else {
                    selectedFilters.add("5");
                    filter.put("stars", starValue);
                }
            } catch (NumberFormatException e) {
                view.showErrorMessage("Number of stars must be a valid number between 1 and 5");
                return;
            }
        }

        filter.put("type", "1");
        filter.put("selectedFilters", selectedFilters);

        serverApi.filter(filter, new ServerAPI.Callback() {
            @Override
            public void onSuccess(Response response) {
                JSONObject body = response.getBody();
                if (body == null) {
                    view.showErrorMessage("No hotels found");
                    view.openHotelsAfterFilter(null);
                } else {
                    JSONArray hotels = (JSONArray) body.get("results");
                    saveHotelsAfterFilter(hotels);
                    view.openHotelsAfterFilter(hotels);
                }
            }

            @Override
            public void onFailure(String error) {
                view.showErrorMessage("Filter failed: " + error);
            }
        });
    }

    /**
     * Saves the filtered hotels into the DAO.
     *
     * @param hotelsArray The JSON array containing the hotel data.
     */
    private void saveHotelsAfterFilter(JSONArray hotelsArray) {
        hotelDAO.deleteAll();
        for (Object hotelObj : hotelsArray) {
            JSONObject hotel = (JSONObject) hotelObj;
            Hotel newHotel = ConvertJSONToHotel.initializeHotel(hotel);
            hotelDAO.save(newHotel);
        }
    }
}
