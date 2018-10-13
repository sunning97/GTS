package vn.edu.ut.gts.views.home.fragments;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeekSchedule extends Fragment {

    @BindView(R.id.student_week_schedule_table) TableLayout studentWeekSheduleTable;
    @BindView(R.id.table_row0) TableRow tableLayout0;
    @BindView(R.id.table_row1) TableRow tableLayout1;
    @BindView(R.id.table_row2) TableRow tableLayout2;
    @BindView(R.id.table_row3) TableRow tableLayout3;
    @BindView(R.id.table_row4) TableRow tableLayout4;
    @BindView(R.id.table_row5) TableRow tableLayout5;
    @BindView(R.id.table_row6) TableRow tableLayout6;
    @BindView(R.id.table_row7) TableRow tableLayout7;

    private List<TableRow> listTableRow = new ArrayList<>();
    private Student student;
    private SweetAlertDialog loadingDialog;
    public WeekSchedule() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_schedule, container, false);
        ButterKnife.bind(this,view);

        init();
        getDataWeekSchedule();

        return view;
    }

    private void getDataWeekSchedule(){
        loadingDialog.show();
        AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray jsonArray = student.getSchedules();
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                generateTableContent(jsonArray);
                loadingDialog.dismiss();
            }
        };
        asyncTask.execute();
    }
    private void generateTableContent(JSONArray jsonArray){

        for(int i = 0;i < jsonArray.length();i++){
            TableRow tableRow = listTableRow.get(i);

            LinearLayout date = (LinearLayout) tableRow.getChildAt(0);
            LinearLayout itemMorning = (LinearLayout) tableRow.getChildAt(1);
            LinearLayout itemAfternoon = (LinearLayout) tableRow.getChildAt(2);
            LinearLayout itemEvening = (LinearLayout) tableRow.getChildAt(3);

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //set date
                TextView tvDate = (TextView) date.getChildAt(1);
                tvDate.setText(jsonObject.getString("date"));

                // set morning
                if(jsonObject.getJSONObject("morning").length() > 0){
                    JSONObject morning = jsonObject.getJSONObject("morning");
                    setOne(morning,itemMorning);
                } else {
                    itemMorning.removeAllViews();
                }
                //set afternoon
                if(jsonObject.getJSONObject("afternoon").length() > 0){
                    JSONObject afternoon = jsonObject.getJSONObject("afternoon");
                    setOne(afternoon,itemAfternoon);
                } else {
                    itemAfternoon.removeAllViews();
                }

                //set evening
                if(jsonObject.getJSONObject("evening").length() > 0){
                    JSONObject evening = jsonObject.getJSONObject("evening");
                    setOne(evening,itemEvening);
                } else {
                    itemEvening.removeAllViews();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setOne(JSONObject jsonObject,LinearLayout linearLayout){
        try {
            TextView subjecID = (TextView) linearLayout.getChildAt(0);
            subjecID.setText(jsonObject.getString("subjectId"));
            TextView subjectName = (TextView) linearLayout.getChildAt(1);
            subjectName.setText(jsonObject.getString("subjectName"));
            TextView subjectTime = (TextView) linearLayout.getChildAt(3);
            subjectTime.setText("Tiết: "+jsonObject.getString("subjectTime"));
            TextView subjectLecturer = (TextView) linearLayout.getChildAt(4);
            subjectLecturer.setText("GV: "+jsonObject.getString("subjectLecturer"));
            TextView subjectRoom = (TextView) linearLayout.getChildAt(5);
            subjectRoom.setText("Phòng: "+jsonObject.getString("subjectRoom"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        student = new Student(getContext());
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);

        listTableRow.add(tableLayout1);
        listTableRow.add(tableLayout2);
        listTableRow.add(tableLayout3);
        listTableRow.add(tableLayout4);
        listTableRow.add(tableLayout5);
        listTableRow.add(tableLayout6);
        listTableRow.add(tableLayout7);
    }
}
