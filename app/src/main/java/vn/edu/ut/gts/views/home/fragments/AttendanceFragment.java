package vn.edu.ut.gts.views.home.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;
import vn.edu.ut.gts.actions.helpers.Storage;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment {

    @BindView(R.id.student_attendance_table)
    TableLayout studentAttendanceTable;
    @BindView(R.id.student_attendance_spinner)
    MaterialSpinner studentAttendanceSpinner;
    Storage storage;
    Student student;
    String[] dataSnpinner = {"Học kỳ 1 năm học 2018-2019","Học kỳ hè năm học 2017-2018","Học kỳ 2 năm học 2017-2018","Học kỳ 1 năm học 2017-2018","Học kỳ hè năm học 2016-2017","Học kỳ 2 năm học 2016-2017","Học kỳ 1 năm học 2016-2017","Học kỳ hè năm học 2015-2016","Học kỳ 2 năm học 2015-2016","Học kỳ 1 năm học 2015-2016"};

    public AttendanceFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.storage = new Storage(getContext());
        this.student = new Student(getContext());
        this.initAttendance();
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        ButterKnife.bind(this,view);

        studentAttendanceSpinner.setItems(dataSnpinner);
        studentAttendanceSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
        this.dataInit();
        //this.generateTableContent(studentAttendanceTable,data);
        return  view;
    }

    private void generateTableContent(TableLayout tableLayout,List<JSONObject> data) {
        int index = 1;
        for (JSONObject jsonObject : data) {
            TableRow tableRow = new TableRow(getContext());
            tableRow.setGravity(Gravity.CENTER);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            tableRow.setMinimumHeight(120);
            if (index % 2 == 0) {
                tableRow.setBackgroundColor(getResources().getColor(R.color.gray));
            }
            try {
                tableRow.addView(generateTableCell(jsonObject.getString("subject_id"), false));
                tableRow.addView(generateTableCell(jsonObject.getString("subject_name"), false));
                tableRow.addView(generateTableCell(jsonObject.getString("dhtv"), true));
                tableRow.addView(generateTableCell(jsonObject.getString("halt_permit"), true));
                tableRow.addView(generateTableCell(jsonObject.getString("halt_no_permit"), true));
            } catch (Exception e) {

            }
        }
    }

    private void initAttendance(){
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                student.getDataTTDiemDanh();
                return null;
            }
        };
        asyncTask.execute();
    }

    private void generateTableContent(TableLayout tableLayout,JSONArray data){
        Log.e("Attendance", data.toString());
        try {
            for (int i = 0; i< data.length(); i++) {

                JSONObject subject = data.getJSONObject(i);

                TableRow tableRow = new TableRow(getContext());
                tableRow.setGravity(Gravity.CENTER);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                tableRow.setMinimumHeight(200);
                if((i+1) % 2 == 0){
                    tableRow.setBackgroundColor(getResources().getColor(R.color.gray));
                }
                try {
                    tableRow.addView(generateTableCell(subject.getString("ma_mon_hoc"),false));
                    tableRow.addView(generateTableCell(subject.getString("ten_mon_hoc"),false));
                    tableRow.addView(generateTableCell(subject.getString("dvht"),true));
                    tableRow.addView(generateTableCell(subject.getString("nghi_co_phep"),true));
                    tableRow.addView(generateTableCell(subject.getString("nghi_co_phep"),true));
                } catch (Exception e){

                }
                tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private LinearLayout generateTableCell(String content,Boolean isMarginCenter){

        // generate cell container
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        if(isMarginCenter) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        // generate cell's text view
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams a = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        a.setMargins(10,0,10,0);
        textView.setLayoutParams(a);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(content);
        linearLayout.addView(textView);
        return linearLayout;
    }

    private void dataInit(){
        AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                return student.getTTDiemDanh();
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                generateTableContent(studentAttendanceTable, jsonArray);
            }
        };
        asyncTask.execute();

    }

}
