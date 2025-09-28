package gr.aueb.bookingapp.frontend.viewadaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gr.aueb.bookingapp.R;
import gr.aueb.bookingapp.backend.entities.Hotel;

/**
 * RecyclerView Adapter for displaying a list of hotels for booking.
 * Handles the binding of hotel data to the view items and user interactions.
 */
public class BookHotelRecyclerViewAdaptor extends RecyclerView.Adapter<BookHotelRecyclerViewAdaptor.ViewHolder> {

    private final List<Hotel> hotels;
    private final HotelSelectionListener listener;
    private final int layoutResourceId;
    private final Context context;

    /**
     * Constructor for initializing the adapter with the required data.
     *
     * @param context The context from which the adapter is created.
     * @param hotels The list of hotels to be displayed.
     * @param listener The listener for hotel selection events.
     * @param layoutResourceId The layout resource ID for the hotel item.
     */
    public BookHotelRecyclerViewAdaptor(Context context, ArrayList<Hotel> hotels, HotelSelectionListener listener, int layoutResourceId) {
        this.hotels = hotels;
        this.context = context;
        this.listener = listener;
        this.layoutResourceId = layoutResourceId;
    }

    @NonNull
    @Override
    public BookHotelRecyclerViewAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHotelRecyclerViewAdaptor.ViewHolder holder, int position) {
        Hotel currentHotel = hotels.get(position);
        holder.hotelName.setText(currentHotel.getHotelName());
        holder.hotelArea.setText(currentHotel.getArea());
        holder.hotelPeople.setText(String.valueOf(currentHotel.getNumPeople()));

        String str_price =  currentHotel.getPrice() + "€ / night";
        holder.hotelPrice.setText(str_price);

        String str_reviews = "  " + currentHotel.getNumReviews() + " reviews";
        holder.hotelReviews.setText(str_reviews);

        holder.hotelStars.setText(String.valueOf(currentHotel.getStars()));

        int imageResourceId = getImageResourceId(context, currentHotel.getRoomImage());
        holder.hotelImage.setImageResource(imageResourceId);

        holder.Button.setOnClickListener(view -> listener.selectedHotel(currentHotel));
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    /**
     * ViewHolder class for the hotel item views.
     * Holds references to the views for each data item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView hotelName;
        public final TextView hotelArea;
        public final TextView hotelPeople;
        public final TextView hotelPrice;
        public final TextView hotelReviews;
        public final TextView hotelStars;
        public final Button Button;
        public final ImageView hotelImage;

        public ViewHolder(View v) {
            super(v);
            hotelName = v.findViewById(R.id.hotelName);
            hotelArea = v.findViewById(R.id.Area);
            hotelPeople = v.findViewById(R.id.NumOfPeople);
            hotelPrice = v.findViewById(R.id.Price);
            hotelReviews = v.findViewById(R.id.Num_of_rates);
            hotelStars = v.findViewById(R.id.Rate);
            Button = v.findViewById(R.id.SameButton);
            hotelImage = v.findViewById(R.id.imageView5);
        }
    }

    /**
     * Listener interface for hotel selection events.
     */
    public interface HotelSelectionListener {
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
