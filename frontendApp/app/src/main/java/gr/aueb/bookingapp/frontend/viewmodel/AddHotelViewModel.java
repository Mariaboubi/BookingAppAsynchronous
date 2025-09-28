package gr.aueb.bookingapp.frontend.viewmodel;

import androidx.lifecycle.ViewModel;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.AddHotelPresenter;

public class AddHotelViewModel extends ViewModel {
    private final AddHotelPresenter addHotelPresenter;

    public AddHotelViewModel() {
        this.addHotelPresenter = new AddHotelPresenter(ServerAPI.getInstance());
    }

    public AddHotelPresenter getPresenter() {
        return addHotelPresenter;
    }
}
