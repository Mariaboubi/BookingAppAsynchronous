package gr.aueb.bookingapp.frontend.viewadaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.simple.JSONObject;

import java.util.List;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.backend.dao.HotelDAO;
import gr.aueb.bookingapp.backend.entities.Hotel;
import gr.aueb.bookingapp.backend.memoryDao.HotelDAOMemory;
import gr.aueb.bookingapp.backend.util.JSONUtils;

/**
 * RecyclerView Adapter for displaying a list of hotel reservations.
 * Handles the binding of reservation data to the view items.
 */
public class ShowReservationsRecyclerViewAdaptor extends RecyclerView.Adapter<ShowReservationsRecyclerViewAdaptor.ViewHolder> {

    private final List<Hotel> hotels;
    private final ReservationSelectionListener listener;
    private final Context context;
    private final int layoutResourceId;
    private final HotelDAO hotelDAO = new HotelDAOMemory();

    /**
     * Constructor for initializing the adapter with the required data.
     *
     * @param context The context from which the adapter is created.
     * @param hotels The list of hotels to be displayed.
     * @param listener The listener for reservation selection events.
     * @param layoutResourceId The layout resource ID for the reservation item.
     */
    public ShowReservationsRecyclerViewAdaptor(Context context, List<Hotel> hotels, ReservationSelectionListener listener, int layoutResourceId) {
        this.hotels = hotels;
        this.context = context;
        this.listener = listener;
        this.layoutResourceId = layoutResourceId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel currentHotel = hotels.get(position);

        // Setting hotel name
        holder.hotelName.setText(currentHotel.getHotelName());

        // Fetching reservations and setting data
        String result = hotelDAO.findReservationsByHotel(currentHotel);
        JSONObject reservations = JSONUtils.parseJSONString(result);

        // Constructing reservation period string
        StringBuilder period = new StringBuilder();

        for (Object key : reservations.keySet()) {
            String reserv = reservations.get(key).toString().replace("[", "").replace("]", "").replace(",", "\n ").replace('"', ' ').replace("}", "");
            period.append("  Client ").append(key).append(":\n ").append(reserv).append("\n\n");
        }

        // Setting the text in the views
        holder.period.setText(period.toString());

        // Setting the hotel image
        int imageResourceId = getImageResourceId(context, currentHotel.getRoomImage());
        holder.hotelImage.setImageResource(imageResourceId);
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    /**
     * ViewHolder class for the reservation item views.
     * Holds references to the views for each data item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView hotelName;
        public final TextView period;
        public final ImageView hotelImage;

        public ViewHolder(View v) {
            super(v);
            hotelName = v.findViewById(R.id.hotelName);
            period = v.findViewById(R.id.ReservationPeriod);
            hotelImage = v.findViewById(R.id.imageView5);
        }
    }

    /**
     * Listener interface for reservation selection events.
     */
    public interface ReservationSelectionListener {
        void selectedHotel(Hotel selectedHotel);
    }

    /**
     * Retrieves the image resource ID for a given image path.
     *
     * @param context The context to access resources.
     * @param imagePath The path of the image.
     * @return The resource ID of the image.
     */
    private int getImageResourceId(Context context, String imagePath) {
        String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1).replace(".png", "");
        return context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
    }
}
