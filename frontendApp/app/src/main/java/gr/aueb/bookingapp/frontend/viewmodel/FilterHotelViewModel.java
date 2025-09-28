package gr.aueb.bookingapp.frontend.viewmodel;

import androidx.lifecycle.ViewModel;

import gr.aueb.bookingapp.backend.memoryDao.HotelDAOMemory;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.FilterHotelPresenter;

public class FilterHotelViewModel extends ViewModel {
    private final FilterHotelPresenter filterHotelPresenter;

    public FilterHotelViewModel() {
        this.filterHotelPresenter = new FilterHotelPresenter(ServerAPI.getInstance(),new HotelDAOMemory());
    }

    public FilterHotelPresenter getPresenter() {
        return filterHotelPresenter;
    }
}
