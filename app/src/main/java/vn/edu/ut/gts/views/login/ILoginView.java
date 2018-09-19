package vn.edu.ut.gts.views.login;

public interface ILoginView {
    public void startLoadingButton();
    public void doneLoadingButton();
    public void revertLoadingButton();
    public void loginSuccess();
    public void loginFailed();
}
