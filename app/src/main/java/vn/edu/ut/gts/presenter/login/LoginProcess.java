package vn.edu.ut.gts.presenter.login;

import android.content.Context;
import android.os.AsyncTask;

import vn.edu.ut.gts.actions.Login;
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.login.ILoginView;

public class LoginProcess implements ILoginProcess{
    private ILoginView iLoginView;
    private Context context;
    private Storage storage;
    private Login actionLogin;
    public LoginProcess(ILoginView iLoginView, Context context) {
        this.iLoginView = iLoginView;
        this.context = context;
        this.storage = new Storage(context);
        this.init();
    }
    private void init(){
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                actionLogin = new Login(context);
                return null;
            }
        };
        asyncTask.execute();
    }



    @Override
    public void execute(final String studentId, final String password) {
        iLoginView.startLoadingButton();
<<<<<<< HEAD
        iLoginView.loginSuccess();
        iLoginView.doneLoadingButton();
//        AsyncTask<Boolean, Void, Boolean> asyncTask = new AsyncTask<Boolean, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Boolean... booleans) {
//                try {
//                    JSONObject dataLogin = new JSONObject(storage.getString("dataLogin","{}"));
//                    boolean status = loginAction.doLogin(studentId, password, dataLogin).checkLogin();
//                    return status;
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return false;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean status) {
//                if(status){
//                    iLoginView.doneLoadingButton();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    iLoginView.loginSuccess();
//                }else{
//                    iLoginView.revertLoadingButton();
//                    iLoginView.loginFailed();
//                }
//            }
//        };
//        asyncTask.execute();
=======
        //iLoginView.loginSuccess();
        //iLoginView.doneLoadingButton();
        //iLoginView.revertLoadingButton();
        AsyncTask<Boolean, Void, Boolean> asyncTask = new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                actionLogin.login(studentId, password);
                return Helper.checkLogin(storage.getCookie());
            }

            @Override
            protected void onPostExecute(Boolean status) {
                if(status){
                    iLoginView.loginSuccess();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    iLoginView.doneLoadingButton();
                }else{
                    iLoginView.revertLoadingButton();
                    iLoginView.loginFailed();
                }
            }
        };
        asyncTask.execute();
>>>>>>> 00643535d6310e8d13d1bfb3f71a3d5ae283700e
    }


}
