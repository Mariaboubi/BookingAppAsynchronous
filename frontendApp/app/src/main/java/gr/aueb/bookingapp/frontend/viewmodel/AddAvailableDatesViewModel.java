package gr.aueb.bookingapp.frontend.viewmodel;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.AddAvailableDatesPresenter;

import androidx.lifecycle.ViewModel;

public class AddAvailableDatesViewModel extends ViewModel {

    private  final AddAvailableDatesPresenter addAvailableDatesPresenter;

    public AddAvailableDatesViewModel() {
        addAvailableDatesPresenter = new AddAvailableDatesPresenter(ServerAPI.getInstance());
    }

    public AddAvailableDatesPresenter getPresenter() {
        return addAvailableDatesPresenter;
    }
}
