package gr.aueb.bookingapp.frontend.viewmodel;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import gr.aueb.bookingapp.frontend.presenter.SignInPresenter;

import androidx.lifecycle.ViewModel;

public class SignInViewModel extends ViewModel {
    private final SignInPresenter signinPresenter;
    public SignInViewModel()
    {
        signinPresenter = new SignInPresenter(ServerAPI.getInstance());
    }
    public SignInPresenter getPresenter() {
        return signinPresenter;
    }
}
