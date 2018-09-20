package vn.edu.ut.gts.presenter.login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import vn.edu.ut.gts.actions.LoginAction;
import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.storage.Storage;
import vn.edu.ut.gts.views.login.ILoginView;

public class LoginProcess implements ILoginProcess{
    private ILoginView iLoginView;
    private Context context;
    private Storage  storage;
    private LoginAction loginAction;
    public LoginProcess(ILoginView iLoginView,Context ct) {
        this.iLoginView = iLoginView;
        this.context = ct;
        this.storage = new Storage(context);
        loginAction = new LoginAction();
        this.checkLogin();
        this.loginInit();
    }
    private void checkLogin(){
        AsyncTask<Void,Void,Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                String cookie = storage.getCookie();
                return loginAction.checkLogin(cookie);
            }

            @Override
            protected void onPostExecute(Boolean status) {
                if(status) iLoginView.loginSuccess();
            }
        };
        asyncTask.execute();
    }
    private void loginInit(){
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                JSONObject dataLogin = loginAction.getDataLogin();
                storage.putString("dataLogin",dataLogin.toString());
                try {
                    storage.setCookie(dataLogin.getString("cookie"));
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
        AsyncTask<Boolean, Void, Boolean> asyncTask = new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                try {
                    JSONObject dataLogin = new JSONObject(storage.getString("dataLogin","{}"));
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
