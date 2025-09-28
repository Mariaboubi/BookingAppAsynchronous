package gr.aueb.bookingapp.frontend.viewmodel;

import androidx.lifecycle.ViewModel;

import gr.aueb.bookingapp.backend.memoryDao.HotelDAOMemory;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.ClientConsolePresenter;

public class ClientConsoleViewModel extends ViewModel {
    private final ClientConsolePresenter presenter;

    public ClientConsoleViewModel() {
        presenter = new ClientConsolePresenter(ServerAPI.getInstance(),new HotelDAOMemory());
    }

    public ClientConsolePresenter getPresenter() {
        return presenter;
    }
}
