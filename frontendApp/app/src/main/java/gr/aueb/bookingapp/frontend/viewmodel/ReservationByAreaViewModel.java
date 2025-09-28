package gr.aueb.bookingapp.frontend.viewmodel;

import androidx.lifecycle.ViewModel;

import gr.aueb.bookingapp.backend.memoryDao.HotelDAOMemory;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.ReservationByAreaPresenter;

public class ReservationByAreaViewModel extends ViewModel {
    private final ReservationByAreaPresenter reservationByAreaPresenter;

    public ReservationByAreaViewModel()
    {
        reservationByAreaPresenter = new ReservationByAreaPresenter(ServerAPI.getInstance());
    }
    public ReservationByAreaPresenter getPresenter() {
        return reservationByAreaPresenter;
    }
}
