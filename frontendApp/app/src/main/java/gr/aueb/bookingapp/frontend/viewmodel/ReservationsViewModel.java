package gr.aueb.bookingapp.frontend.viewmodel;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.ReservationsPresenter;

import androidx.lifecycle.ViewModel;
public class ReservationsViewModel extends ViewModel {

    private final ReservationsPresenter reservationsPresenter;
    public ReservationsViewModel()
    {
        reservationsPresenter = new ReservationsPresenter(ServerAPI.getInstance());
    }
    public ReservationsPresenter getPresenter() {
        return reservationsPresenter;
    }
}
