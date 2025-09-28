package gr.aueb.bookingapp.backend.dao;

import java.util.ArrayList;
import java.util.List;

import gr.aueb.bookingapp.backend.entities.Hotel;

public interface HotelDAO {

    /**
     * Finds all Hotel entities in the storage.
     *
     * @return An ArrayList of all Hotel entities.
     */
    ArrayList<Hotel> findAll();

    /**
     * Deletes all Hotel entities from the storage.
     */
    void deleteAll();

    /**
     * Saves a Hotel entity to the storage.
     *
     * @param entity The Hotel entity to be saved.
     */
    void save(Hotel entity);


    /**
     * Associates a Hotel with a list of reservation dates.
     *
     * @param hotelName The name of the Hotel.
     * @param dates The list of reservation dates to be associated with the Hotel.
     */
    void associateHotelWithReservations(String hotelName, List<String> dates);

    /**
     * Associates a Hotel with a specific reservation.
     *
     * @param hotel The Hotel entity to be associated.
     * @param reservation The reservation to be associated with the Hotel.
     */
    void associateHotelWithShowReservation(Hotel hotel, String reservation);

    /**
     * Finds all reservations associated with a Hotel by its name.
     *
     * @param hotelName The name of the Hotel.
     * @return A list of reservations associated with the Hotel.
     */
    List<String> findReservationsByHotelName(String hotelName);

    /**
     * Finds reservations associated with a specific Hotel entity.
     *
     * @param hotel The Hotel entity.
     * @return A string representing the reservations associated with the Hotel.
     */
    String findReservationsByHotel(Hotel hotel);
}
