package vn.edu.ut.gts.presenter.home;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import vn.edu.ut.gts.actions.StudentInfoAction;
import vn.edu.ut.gts.storage.Storage;
import vn.edu.ut.gts.views.homes.fragments.StudentInfoRootFragment;

public class StudentInfoProcess {
    private Context context;
    private Storage storage;
    private StudentInfoAction studentInfoAction;
    public StudentInfoProcess(Context context){
        this.context = context;
        this.storage = new Storage(context);
        this.studentInfoAction = new StudentInfoAction();
    }


    public void loadStudentData(){
        AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                String cookie = storage.getString("tmp","cookie","");
                studentInfoAction.getStudentProfile(cookie);
                return null;
            }
        };
    }
}
