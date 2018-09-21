package vn.edu.ut.gts.presenter.home;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.edu.ut.gts.actions.StudentInfoAction;
import vn.edu.ut.gts.storage.Storage;

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
        AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                String cookie = storage.getCookie();
                JSONArray dataStudent = studentInfoAction.getStudentProfile(cookie);
                return dataStudent;
            }

            @Override
            protected void onPostExecute(JSONArray dataLogin) {

            }
        };

        asyncTask.execute();
    }
}
