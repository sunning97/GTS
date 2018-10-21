package vn.edu.ut.gts.views.home.fragments;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;
import vn.edu.ut.gts.helpers.EpicDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrameProgramFragment extends Fragment {

    private Student student;
    private EpicDialog epicDialog;
    private JSONObject data;
    private SweetAlertDialog loadingDialog;

    public FrameProgramFragment() {

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_frame_program_toolbar_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        student = new Student(getContext());
        View view = inflater.inflate(R.layout.fragment_frame_program, container, false);
        init();
        setHasOptionsMenu(true);
        getDataFrameProgram();
        return view;
    }

    private void init() {
        epicDialog = new EpicDialog(getContext());
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);
    }


    private void getDataFrameProgram(){
        AsyncTask<Void,Void,JSONObject> getData = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject jsonObject = student.getFrameProgram();
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                data = jsonObject;
                loadingDialog.dismiss();
            }
        };
        getData.execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.infor: {
                try {
                    String titleAll = data.getString("info");
                    String[] parts = titleAll.split("-");
                    String title1 = "";
                    for (int i = 0;i <= parts.length-2;i++){
                        title1+=parts[i].trim()+" - ";
                    }
                    epicDialog.showFrameProgramInfoDialog(title1.trim(),parts[parts.length-1].trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return true;
    }
}
