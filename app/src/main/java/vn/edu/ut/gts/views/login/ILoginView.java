package vn.edu.ut.gts.views.login;

public interface ILoginView {
    public void startLoadingButton();
    public void doneLoadingButton();
    public void revertLoadingButton();
    public void loginSuccess();
    public void loginFailed();
    public void setLastLogin();
    public void showLoadingDialog();
    public void dismisLoadingDialog();
    public void showError();
    public void transferToRetryBtn();
    public void transferToLoginBtn();
}
