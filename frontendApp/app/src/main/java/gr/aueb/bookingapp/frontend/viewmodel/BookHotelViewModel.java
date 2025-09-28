package gr.aueb.bookingapp.frontend.viewmodel;

import androidx.lifecycle.ViewModel;

import gr.aueb.bookingapp.backend.memoryDao.HotelDAOMemory;
import gr.aueb.bookingapp.frontend.presenter.BookHotelPresenter;

public class BookHotelViewModel extends ViewModel {

    private final BookHotelPresenter presenter;
    /**
     * Initializes the presenter by passing new dao's as parameters
     */
    public BookHotelViewModel() {
        presenter = new BookHotelPresenter(new HotelDAOMemory());
    }

    /**
     * @return returns the presenter that stores the data
     */
    public BookHotelPresenter getPresenter() {
        return presenter;
    }
}
