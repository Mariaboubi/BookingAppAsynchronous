package gr.aueb.bookingapp.frontend.viewmodel;

import gr.aueb.bookingapp.frontend.api.ServerAPI;
import androidx.lifecycle.ViewModel;
import gr.aueb.bookingapp.frontend.presenter.SignUpPresenter;
public class SignUpViewModel extends ViewModel{
    private final SignUpPresenter signupPresenter;
    public SignUpViewModel()
    {
        signupPresenter = new SignUpPresenter(ServerAPI.getInstance());
    }
    public SignUpPresenter getPresenter() {
        return signupPresenter;
    }
}
