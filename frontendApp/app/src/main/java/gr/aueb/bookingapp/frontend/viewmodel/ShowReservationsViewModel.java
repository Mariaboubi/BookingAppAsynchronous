package gr.aueb.bookingapp.frontend.viewmodel;

import gr.aueb.bookingapp.backend.memoryDao.HotelDAOMemory;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.ShowReservationsPresenter;

import androidx.lifecycle.ViewModel;
public class ShowReservationsViewModel extends ViewModel {

    private final ShowReservationsPresenter showReservationsPresenter;

    public ShowReservationsViewModel() {
        showReservationsPresenter = new ShowReservationsPresenter(ServerAPI.getInstance(),new HotelDAOMemory());
    }

    public ShowReservationsPresenter getPresenter() {return showReservationsPresenter;}
}