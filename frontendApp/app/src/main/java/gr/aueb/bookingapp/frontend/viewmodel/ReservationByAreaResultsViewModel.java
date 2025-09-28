package gr.aueb.bookingapp.frontend.viewmodel;

import androidx.lifecycle.ViewModel;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.ReservationByAreaResultsPresenter;

public class ReservationByAreaResultsViewModel extends ViewModel {
    private final ReservationByAreaResultsPresenter makeReservationPresenter;

    public ReservationByAreaResultsViewModel()
    {
        makeReservationPresenter = new ReservationByAreaResultsPresenter(ServerAPI.getInstance());
    }
    public ReservationByAreaResultsPresenter getPresenter() {
        return makeReservationPresenter;
    }
}
