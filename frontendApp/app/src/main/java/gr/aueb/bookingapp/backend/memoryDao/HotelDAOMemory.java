package gr.aueb.bookingapp.backend.memoryDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.aueb.bookingapp.backend.dao.HotelDAO;
import gr.aueb.bookingapp.backend.entities.Hotel;

/**
 * In-memory implementation of the HotelDAO interface.
 * This class provides methods to manage Hotel entities and their reservations in memory.
 */
public class HotelDAOMemory implements HotelDAO {

    // List to store hotel entities
    protected static ArrayList<Hotel> entities = new ArrayList<>();

    // Map to store hotel reservations with hotel name as the key and list of dates as the value
    protected static Map<String, List<String>> reservations = new HashMap<>();

    // Map to store show reservations with Hotel object as the key and reservation string as the value
    protected static Map<Hotel, String> showReservations = new HashMap<>();

    /**
     * Deletes all hotel entities and reservations from the storage.
     */
    @Override
    public void deleteAll() {
        entities.clear();
        reservations.clear();
        showReservations.clear();
    }

    /**
     * Saves a Hotel entity to the storage.
     *
     * @param entity The Hotel entity to be saved.
     */
    @Override
    public void save(Hotel entity) {
        entities.add(entity);
    }

    /**
     * Finds all Hotel entities in the storage.
     *
     * @return An ArrayList of all Hotel entities.
     */
    @Override
    public ArrayList<Hotel> findAll() {
        return entities;
    }


    /**
     * Associates a Hotel with a list of reservation dates.
     *
     * @param hotelName The name of the Hotel.
     * @param dates The list of reservation dates to be associated with the Hotel.
     */
    @Override
    public void associateHotelWithReservations(String hotelName, List<String> dates) {
        reservations.put(hotelName, dates);
    }

    /**
     * Associates a Hotel with a specific reservation.
     *
     * @param hotel The Hotel entity to be associated.
     * @param reservation The reservation to be associated with the Hotel.
     */
    @Override
    public void associateHotelWithShowReservation(Hotel hotel, String reservation) {
        showReservations.put(hotel, reservation);
    }

    /**
     * Finds all reservations associated with a Hotel by its name.
     *
     * @param hotelName The name of the Hotel.
     * @return A list of reservations associated with the Hotel, or null if none found.
     */
    @Override
    public List<String> findReservationsByHotelName(String hotelName) {
        return reservations.getOrDefault(hotelName, null);
    }

    /**
     * Finds reservations associated with a specific Hotel entity.
     *
     * @param hotel The Hotel entity.
     * @return A string representing the reservations associated with the Hotel, or null if none found.
     */
    @Override
    public String findReservationsByHotel(Hotel hotel) {
        return showReservations.getOrDefault(hotel, null);
    }
}
