package vn.edu.ut.gts.views.login;

public interface ILoginView {
    void startLoadingButton();

    void doneLoadingButton();

    void revertLoadingButton();

    void loginSuccess();

    void loginFailed();

    void setLastLogin();

    void showLoadingDialog();

    void dismisLoadingDialog();

    void showTimeoutDialog();

    void showNoInternetDialog();

    void transferToRetryBtn();

    void transferToLoginBtn();

    void showLoginLayout();

    void showAutoLoginLayout();

    void showLoginAutoErrorDialog();

    void disableInput();
    void enableInput();
}
