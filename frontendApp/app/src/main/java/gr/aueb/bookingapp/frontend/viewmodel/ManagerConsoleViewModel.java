package gr.aueb.bookingapp.frontend.viewmodel;

import gr.aueb.bookingapp.backend.memoryDao.HotelDAOMemory;
import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.ManagerConsolePresenter;

import androidx.lifecycle.ViewModel;

public class ManagerConsoleViewModel extends ViewModel {

    private final ManagerConsolePresenter managerConsolePresenter;

    public ManagerConsoleViewModel()
    {
        managerConsolePresenter = new ManagerConsolePresenter(new HotelDAOMemory(),ServerAPI.getInstance());
    }
    public ManagerConsolePresenter getPresenter() {
        return managerConsolePresenter;
    }


}
