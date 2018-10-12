package vn.edu.ut.gts.presenters.login;

import android.content.Context;
import android.os.AsyncTask;

import vn.edu.ut.gts.actions.Login;
import vn.edu.ut.gts.actions.Student;
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.login.ILoginView;

public class LoginProcess implements ILoginProcess{
    private ILoginView iLoginView;
    private Context context;
    private Storage storage;
    private Login actionLogin;
    private Student student;
    public LoginProcess(ILoginView iLoginView, Context context) {
        this.iLoginView = iLoginView;
        this.context = context;
        this.storage = new Storage(context);
        student = new Student(context);
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

        AsyncTask<Boolean, Void, Boolean> asyncTask = new AsyncTask<Boolean, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... booleans) {
                actionLogin.login(studentId, password);
                return Helper.checkLogin(storage.getCookie());
            }

            @Override
            protected void onPostExecute(Boolean status) {
                if(status){
                    saveLastLoginID(studentId);
                    saveCurrentStudentName();
                    iLoginView.doneLoadingButton();
                    iLoginView.loginSuccess();
                }else{
                    iLoginView.revertLoadingButton();
                    iLoginView.loginFailed();
                }
            }
        };
        asyncTask.execute();
    }

    private void saveCurrentStudentName(){
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String name = student.getStudentName();
                return name;
            }

            @Override
            protected void onPostExecute(String s) {
                storage.putString("student_name",s);
            }
        };
        asyncTask.execute();
    }
    private void saveLastLoginID(String ID){
        this.storage.putString("last_student_login",ID);
    }
}
