package gr.aueb.bookingapp.frontend.presenter;

import org.json.simple.JSONObject;

import gr.aueb.bookingapp.backend.util.Response;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.view.AddHotelView;

/**
 * Presenter class for handling the addition of a new hotel.
 * Interacts with the AddHotelView and ServerAPI to process user input and server responses.
 */
public class AddHotelPresenter {
    private AddHotelView view;
    private final ServerAPI serverApi;

    /**
     * Constructor to initialize the presenter with the ServerAPI instance.
     *
     * @param serverApi The ServerAPI instance for handling server communication.
     */
    public AddHotelPresenter(ServerAPI serverApi) {
        this.serverApi = serverApi;
    }

    /**
     * Gets the current view associated with the presenter.
     *
     * @return The current AddHotelView instance.
     */
    public AddHotelView getView() {
        return view;
    }

    /**
     * Sets the view for the presenter.
     *
     * @param view The AddHotelView instance to set.
     */
    public void setView(AddHotelView view) {
        this.view = view;
    }

    /**
     * Processes the addition of a new hotel.
     * Validates the input fields and sends a request to add the hotel to the server.
     */
    public void addHotel() {
        String area = view.extractArea();
        String name = view.extractName();
        String numOfPeople = view.extractPeople();
        String price = view.extractPrice();
        String image = view.extractImage();

        JSONObject newHotel = new JSONObject();

        if (name.isEmpty() || area.isEmpty() || numOfPeople.isEmpty() || price.isEmpty() || image.isEmpty()) {
            view.showErrorMessage("Complete all the fields");
            return;
        }

        if (area.length() < 3) {
            view.showErrorMessage("Area must have at least 3 characters");
            return;
        } else {
            newHotel.put("area", area);
        }

        if (name.length() < 3) {
            view.showErrorMessage("Name of the hotel must have at least 3 characters");
            return;
        } else {
            newHotel.put("hotelName", name);
        }

        int people;
        try {
            people = Integer.parseInt(numOfPeople);
            if (people < 1) {
                view.showErrorMessage("Number of People must be a positive integer");
                return;
            } else {
                newHotel.put("numPeople", people);
            }
        } catch (NumberFormatException e) {
            view.showErrorMessage("Number of People must be a valid integer");
            return;
        }

        double priceValue;
        try {
            priceValue = Double.parseDouble(price);
            if (priceValue < 1.0) {
                view.showErrorMessage("Price must be a positive number");
                return;
            } else {
                newHotel.put("price", priceValue);
            }
        } catch (NumberFormatException e) {
            view.showErrorMessage("Price must be a valid number");
            return;
        }

        if (!image.contains("hotel_images")) {
            view.showErrorMessage("Image must be given with the path (hotel_images)");
            return;
        } else {
            newHotel.put("hotelImage", image);
        }

        newHotel.put("type", "1");
        newHotel.put("stars", 0);
        newHotel.put("numReviews", 0);

        serverApi.addHotel(newHotel, new ServerAPI.Callback() {
            @Override
            public void onSuccess(Response response) {
                view.openManagerConsoleActivity();
            }

            @Override
            public void onFailure(String error) {
                view.showErrorMessage("Error: " + error);
            }
        });
    }
}
