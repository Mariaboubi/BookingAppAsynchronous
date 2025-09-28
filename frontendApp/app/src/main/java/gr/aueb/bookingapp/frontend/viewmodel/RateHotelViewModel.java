package gr.aueb.bookingapp.frontend.viewmodel;

import androidx.lifecycle.ViewModel;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.RateHotelPresenter;

public class RateHotelViewModel extends ViewModel {
    private final RateHotelPresenter rateHotelPresenter;

    public RateHotelViewModel()
    {
        rateHotelPresenter = new RateHotelPresenter(ServerAPI.getInstance());
    }
    public RateHotelPresenter getPresenter() {
        return rateHotelPresenter;
    }
}
