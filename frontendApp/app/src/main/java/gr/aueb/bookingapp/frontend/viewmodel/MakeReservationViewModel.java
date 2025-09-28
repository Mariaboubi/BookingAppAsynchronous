package gr.aueb.bookingapp.frontend.viewmodel;

import androidx.lifecycle.ViewModel;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.MakeReservationPresenter;

public class MakeReservationViewModel extends ViewModel {
    private final MakeReservationPresenter makeReservationPresenter;

    public MakeReservationViewModel()
    {
        makeReservationPresenter = new MakeReservationPresenter(ServerAPI.getInstance());
    }
    public MakeReservationPresenter getPresenter() {
        return makeReservationPresenter;
    }
}
