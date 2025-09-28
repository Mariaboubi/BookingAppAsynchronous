package gr.aueb.bookingapp.frontend.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import gr.aueb.bookingapp.backend.util.Response;

/**
 * Singleton class for handling server communication.
 * Provides methods for connecting to the server and sending requests.
 */
public class ServerAPI {

    private static final String HOST = "10.0.2.2";
    private static final int PORT = 8000;
    private final Handler mainHandler = new Handler(Looper.getMainLooper(), msg -> false);

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private static ServerAPI instance;

    /**
     * Callback interface for handling server responses.
     */
    public interface Callback {
        void onSuccess(Response response) throws ParseException;
        void onFailure(String error);
    }

    // Private constructor to prevent instantiation
    private ServerAPI() {
        connect();
    }

    /**
     * Provides the single instance of ServerAPI.
     *
     * @return The single instance of ServerAPI.
     */
    public static synchronized ServerAPI getInstance() {
        Log.i("ServerAPI", "getInstance");
        if (instance == null) {
            instance = new ServerAPI();
        }
        return instance;
    }

    // Connects to the server
    private void connect() {
        new Thread(() -> {
            try {
                socket = new Socket(HOST, PORT);
                outputStream = new DataOutputStream(socket.getOutputStream());
                inputStream = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                Log.e("ServerAPI", "Failed to connect to the server: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Sends a request to the server and handles the response using the provided callback.
     *
     * @param request  The JSON request to send.
     * @param callback The callback to handle the server response.
     */
    public void sendRequest(final JSONObject request, final Callback callback) {
        new Thread(() -> {
            try {
                if (socket == null || socket.isClosed() || !socket.isConnected()) {
                    connect();
                }

                // Send request
                outputStream.writeUTF(request.toJSONString());
                outputStream.flush();

                // Await response
                String responseStr = inputStream.readUTF();
                final Response response = new Response(responseStr);

                // Use Handler to post the response back to the main thread
                mainHandler.post(() -> {
                    try {
                        callback.onSuccess(response);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (final Exception e) {
                mainHandler.post(() -> callback.onFailure(e.getMessage()));
            }
        }).start();
    }

    /**
     * Sends a login request to the server.
     *
     * @param username The username.
     * @param password The password.
     * @param callback The callback to handle the server response.
     */
    public void login(String username, String password, Callback callback) {
        JSONObject request = new JSONObject();
        request.put("type", "login");
        request.put("username", username);
        request.put("password", password);

        sendRequest(request, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                JSONObject body = response.getBody();
                String result = (String) body.get("result");

                if ("authenticated".equals(result)) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure("Authentication failed");
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a logout request to the server.
     *
     * @param callback The callback to handle the server response.
     */
    public void logOut(Callback callback) {
        JSONObject request = new JSONObject();
        request.put("type", "-1");

        sendRequest(request, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a user registration request to the server.
     *
     * @param name     The user's name.
     * @param surname  The user's surname.
     * @param username The user's username.
     * @param password The user's password.
     * @param role     The user's role.
     * @param callback The callback to handle the server response.
     */
    public void registerUser(String name, String surname, String username, String password, String role, Callback callback) {
        JSONObject request = new JSONObject();
        request.put("type", "register");
        request.put("name", name);
        request.put("lastname", surname);
        request.put("username", username);
        request.put("password", password);
        request.put("user_role", role);

        sendRequest(request, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                JSONObject body = response.getBody();
                String result = (String) body.get("result");

                if ("registered".equals(result)) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure("Registration failed");
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a reservation request to the server.
     *
     * @param date      The reservation date.
     * @param hotelName The hotel name.
     * @param callback  The callback to handle the server response.
     */
    public void reservation(String date, String hotelName, Callback callback) {
        JSONObject request = new JSONObject();
        request.put("type", "2");
        request.put("hotelName", hotelName);
        request.put("Reservation dates", date);

        sendRequest(request, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                Response.Status result_state = response.getStatus();
                if (result_state == Response.Status.SUCCESS) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure("Try again!" + response.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a reservation by area request to the server.
     *
     * @param period   The reservation period.
     * @param callback The callback to handle the server response.
     */
    public void reservationByArea(String period, Callback callback) {
        JSONObject request = new JSONObject();
        request.put("type", "4");
        request.put("Period", period);

        sendRequest(request, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                Response.Status result_state = response.getStatus();
                if (result_state == Response.Status.SUCCESS) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure(response.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a request to receive the list of hotels from the server.
     *
     * @param callback The callback to handle the server response.
     */
    public void receiveHotels(Callback callback) {
        JSONObject request = new JSONObject();
        request.put("type", "4");

        sendRequest(request, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                Response.Status result_state = response.getStatus();
                if (result_state == Response.Status.SUCCESS) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure(response.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a request to show reservations from the server.
     *
     * @param callback The callback to handle the server response.
     */
    public void showReservations(Callback callback) {
        JSONObject request = new JSONObject();
        request.put("type", "3");
        sendRequest(request, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a request to receive hotels for rating from the server.
     *
     * @param callback The callback to handle the server response.
     */
    public void receiveHotelsRate(Callback callback) {
        JSONObject request = new JSONObject();
        request.put("type", "5");

        sendRequest(request, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                callback.onSuccess(response);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a rating request to the server.
     *
     * @param rate      The rating value.
     * @param hotelName The hotel name.
     * @param callback  The callback to handle the server response.
     */
    public void rate(double rate, String hotelName, Callback callback) {
        JSONObject request = new JSONObject();
        request.put("type", "3");
        request.put("hotelName", hotelName);
        request.put("newRating", rate);

        sendRequest(request, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                Response.Status result_state = response.getStatus();
                if (result_state == Response.Status.SUCCESS) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure(response.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a filter request to the server.
     *
     * @param filter   The filter criteria.
     * @param callback The callback to handle the server response.
     */
    public void filter(JSONObject filter, Callback callback) {
        sendRequest(filter, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                Response.Status result_state = response.getStatus();
                if (result_state == Response.Status.SUCCESS) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure("No results found");
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a request to add available dates for a hotel to the server.
     *
     * @param addDates The dates to add.
     * @param callback The callback to handle the server response.
     */
    public void addAvailableDates(JSONObject addDates, Callback callback) {
        sendRequest(addDates, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                Response.Status result_state = response.getStatus();
                if (result_state == Response.Status.SUCCESS) {
                    callback.onSuccess(response);
                } else {
                    if (result_state == Response.Status.UNSUCCESSFUL) {
                        callback.onFailure("You are not authorized to add dates to this hotel");
                    } else if (result_state == Response.Status.NOT_FOUND) {
                        callback.onFailure("Hotel not found");
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Sends a request to add a hotel to the server.
     *
     * @param newHotel The hotel details to add.
     * @param callback The callback to handle the server response.
     */
    public void addHotel(JSONObject newHotel, Callback callback) {
        sendRequest(newHotel, new Callback() {
            @Override
            public void onSuccess(Response response) throws ParseException {
                Response.Status result_state = response.getStatus();
                if (result_state == Response.Status.SUCCESS) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure("Hotel not added");
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }
}
