package vn.edu.ut.gts.presenter.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Login;
import vn.edu.ut.gts.actions.LoginAction;
import vn.edu.ut.gts.storage.Storage;
import vn.edu.ut.gts.views.homes.HomeActivity;
import vn.edu.ut.gts.views.login.ILoginView;
import vn.edu.ut.gts.views.login.LoginActivity;

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
                return null;
            }
        };
        asyncTask.execute();
    }


    @Override
    public void loadDataLogin() {
        AsyncTask<String,Void,String> asyncTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                JSONObject dataLogin = loginAction.getDataLogin();
                storage.putString("tmp", "dataLogin", dataLogin.toString());
                return null;
            }
        };
        asyncTask.execute();
    }

    @Override
    public void doLogin(final String studentId, final String password) {
        AsyncTask<Boolean, Void, Boolean> asyncTask = new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                try {
                    JSONObject dataLogin = new JSONObject(storage.getString("tmp","dataLogin","{}"));
                    boolean status = loginAction.doLogin(studentId, password, dataLogin).checkLogin();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
        asyncTask.execute();
//        AsyncTask<String,Void,String> asyncTask =  new AsyncTask<String, Void, String>() {
//            @Override
//            protected String doInBackground(String... strings) {
//                try {
//                    SharedPreferences preferences = context.getSharedPreferences("tmp",Context.MODE_PRIVATE);
//                    JSONObject dataLogin = new JSONObject(preferences.getString("dataLogin","{}"));
//
//                    if(Login.doLogin(studentId,password, dataLogin)){
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                iLoginView.doneLoadingButton();
//            }
//        };
//
//        asyncTask.execute("aa");

        // coi nhu login thanh cong, may cai asyntask o tren thi tach ra class rieng luon, de trong model -> goi vao
    }


}
