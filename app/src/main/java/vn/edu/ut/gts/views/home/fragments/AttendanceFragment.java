package vn.edu.ut.gts.views.home.fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;
import vn.edu.ut.gts.actions.helpers.Storage;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment {

    @BindView(R.id.student_total_halt_date) TextView tvStudentTotalDaltDate;
    @BindView(R.id.student_attendance_table)
    TableLayout studentAttendanceTable;
    @BindView(R.id.student_attendance_spinner)
    MaterialSpinner studentAttendanceSpinner;

    Storage storage;
    Student student;
    private int totalHaltDate = 0;
    private JSONArray semesters;
    List<String> dataSnpinner = new ArrayList<>();
    List<String> headerText = new ArrayList<>();
    SweetAlertDialog loadingDialog;

    public AttendanceFragment() {
        headerText.add("Mã môn học");
        headerText.add("Tên môn học");
        headerText.add("ĐVHT");
        headerText.add("Có phép");
        headerText.add("Ko phép");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        ButterKnife.bind(this,view);
        this.storage = new Storage(getContext());
        this.student = new Student(getContext());
        this.initLoadingDialog();
        this.initAttendance();

        try {
            JSONObject dataDiemDanh = new JSONObject(storage.getString("dataAttendance"));
            semesters = new JSONArray(dataDiemDanh.getString("semesters"));
            for (int i = 0; i < semesters.length(); i++) {
                JSONObject jsonObject = (JSONObject) semesters.get(i);
                dataSnpinner.add(jsonObject.getString("text"));
            }
        } catch (Exception e){}
        studentAttendanceSpinner.setItems(dataSnpinner);

        this.dataInit(0);
        studentAttendanceSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                loadingDialog.show();
                dataInit(position);
            }
        });
        return  view;
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
        tableLayout.removeAllViews();
        totalHaltDate = 0;
        tableLayout.addView(this.generateTableHeader());
        try {
            for (int i = 0; i< data.length(); i++) {

                JSONObject subject = data.getJSONObject(i);

                TableRow tableRow = new TableRow(getContext());
                tableRow.setGravity(Gravity.CENTER);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                tableRow.setMinimumHeight(120);
                if((i+1) % 2 == 0){
                    tableRow.setBackgroundColor(getResources().getColor(R.color.gray));
                }
                try {
                    tableRow.addView(generateTableCell(subject.getString("ma_mon_hoc"),false));
                    tableRow.addView(generateTableCell(subject.getString("ten_mon_hoc"),false));
                    tableRow.addView(generateTableCell(subject.getString("dvht"),true));
                    tableRow.addView(generateTableCell(subject.getString("nghi_co_phep"),true));
                    tableRow.addView(generateTableCell(subject.getString("nghi_ko_phep"),true));
                    if(Integer.parseInt(subject.getString("nghi_co_phep")) > 0) totalHaltDate+= Integer.parseInt(subject.getString("nghi_co_phep"));
                    if(Integer.parseInt(subject.getString("nghi_ko_phep")) > 0) totalHaltDate+= Integer.parseInt(subject.getString("nghi_co_phep"));
                } catch (Exception e){

                }
                tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                tvStudentTotalDaltDate.setText(String.valueOf(totalHaltDate));
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
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        if(isMarginCenter) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        // generate cell's text view
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams a = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        a.setMargins(10,0,10,0);
        textView.setLayoutParams(a);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(content);
        linearLayout.addView(textView);
        return linearLayout;
    }

    private TableRow generateTableHeader(){
        TableRow header = new TableRow(getContext());
        header.setGravity(Gravity.CENTER);
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setMinimumHeight(150);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));


        for (String text:headerText) {

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(5,0,5,0);
            linearLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(textViewLayout);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTypeface(textView.getTypeface(),Typeface.BOLD);
            textView.setText(text);
            linearLayout.addView(textView);

            header.addView(linearLayout);
        }

        return  header;
    }

    private void dataInit(final int pos){
        AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                return student.getTTDiemDanh(pos);
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                generateTableContent(studentAttendanceTable, jsonArray);
                if(loadingDialog != null)
                loadingDialog.dismiss();
            }
        };
        asyncTask.execute();
    }

    private void initLoadingDialog(){
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);
    }
}
