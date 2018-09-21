package vn.edu.ut.gts.presenter.login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import vn.edu.ut.gts.actions.LoginAction;
import vn.edu.ut.gts.storage.Storage;
import vn.edu.ut.gts.views.login.ILoginView;

public class LoginProcess implements ILoginProcess{
    private ILoginView iLoginView;
    private Context context;
    private Storage  storage;
    private LoginAction loginAction;
    public LoginProcess(ILoginView iLoginView,Context context){
        this.iLoginView = iLoginView;
        this.context = context;
        this.storage = new Storage(context);
        this.loginInit();
    }
    private void loginInit(){
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                loginAction = new LoginAction();
                JSONObject dataLogin = loginAction.getDataLogin();
                storage.putString("tmp","dataLogin",dataLogin.toString());
                try {
                    storage.putString("tmp","cookie",dataLogin.getString("cookie"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        asyncTask.execute();
    }

    @Override
    public void doLogin(final String studentId, final String password) {
        iLoginView.startLoadingButton();
        iLoginView.loginSuccess();
        AsyncTask<Boolean, Void, Boolean> asyncTask = new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                try {
                    JSONObject dataLogin = new JSONObject(storage.getString("tmp","dataLogin","{}"));
                    boolean status = loginAction.doLogin(studentId, password, dataLogin).checkLogin();
                    return status;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean status) {
                if(status){
                    iLoginView.doneLoadingButton();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    iLoginView.loginSuccess();
                }else{
                    iLoginView.revertLoadingButton();
                    iLoginView.loginFailed();
                }
            }
        };
        asyncTask.execute();
    }


}
