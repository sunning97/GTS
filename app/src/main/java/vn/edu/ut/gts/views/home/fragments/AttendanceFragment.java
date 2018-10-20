package vn.edu.ut.gts.views.home.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.viethoa.DialogUtils;

import org.jetbrains.annotations.Nullable;
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

    private float dp;
    private int totalHaltDate = 0;
    private JSONArray semesters;

    List<String> dataSnpinner = new ArrayList<>();
    List<String> headerText = new ArrayList<>();

    SweetAlertDialog loadingDialog;

    public AttendanceFragment() {
        headerText.add("Tên môn học");
        headerText.add("ĐVHT");
        headerText.add("Có phép");
        headerText.add("Ko phép");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        ButterKnife.bind(this,view);
        dp = getContext().getResources().getDisplayMetrics().density;
        this.storage = new Storage(getContext());
        this.student = new Student(getContext());
        this.init();
        this.initAttendance();

        this.dataInit(0);
        studentAttendanceSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
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

            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONObject dataDiemDanh = new JSONObject(storage.getString("dataAttendance"));
                    semesters = new JSONArray(dataDiemDanh.getString("semesters"));
                    for (int i = 0; i < semesters.length(); i++) {
                        JSONObject jsonObject = (JSONObject) semesters.get(i);
                        dataSnpinner.add(jsonObject.getString("text"));
                    }
                } catch (Exception e){}
                studentAttendanceSpinner.setItems(dataSnpinner);
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

                final JSONObject subject = data.getJSONObject(i);

                TableRow tableRow = new TableRow(getContext());
                tableRow.setGravity(Gravity.CENTER);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                tableRow.setMinimumHeight((int) dp*60);
                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attendanceDetailShow(subject);
                    }
                });
                if((i+1) % 2 == 0){
                    tableRow.setBackgroundColor(getResources().getColor(R.color.gray));
                }
                try {
                    tableRow.addView(generateTableCell(subject.getString("ten_mon_hoc"),false, (int) (getScreenWidthInDPs(getContext())*0.4)));
                    tableRow.addView(generateTableCell(subject.getString("dvht"),true,(int) (getScreenWidthInDPs(getContext())*0.2)));
                    tableRow.addView(generateTableCell(subject.getString("nghi_co_phep"),true,(int) (getScreenWidthInDPs(getContext())*0.2)));
                    tableRow.addView(generateTableCell(subject.getString("nghi_ko_phep"),true,(int) (getScreenWidthInDPs(getContext())*0.2)));
                    if(Integer.parseInt(subject.getString("nghi_co_phep")) > 0) totalHaltDate+= Integer.parseInt(subject.getString("nghi_co_phep"));
                    if(Integer.parseInt(subject.getString("nghi_ko_phep")) > 0) totalHaltDate+= Integer.parseInt(subject.getString("nghi_co_phep"));
                } catch (Exception e){

                }
                tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            }
            tvStudentTotalDaltDate.setText(String.valueOf(totalHaltDate));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private LinearLayout generateTableCell(String content, Boolean isMarginCenter,int width){

        // generate cell container
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        if(isMarginCenter) layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = (int) (width*dp);

        linearLayout.setLayoutParams(layoutParams);

        // generate cell's text view
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams a = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        a.setMargins((int) dp,0,(int) dp,0);
        textView.setLayoutParams(a);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(content);
        linearLayout.addView(textView);
        return linearLayout;
    }

    private TableRow generateTableHeader(){
        TableRow header = new TableRow(getContext());
        header.setGravity(Gravity.CENTER);
        header.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        header.setMinimumHeight((int)dp*60);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));


        for (String text:headerText) {

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins((int)dp*5,0,(int)dp*5,0);
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
            protected void onPreExecute() {
                loadingDialog.show();
            }

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

    private void init(){
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);
    }

    protected void attendanceDetailShow(JSONObject jsonObject) {
        LayoutInflater factory = getLayoutInflater();
        View view = factory.inflate(R.layout.student_attendance_detail_dialog, null);
        TextView maMonHoc = view.findViewById(R.id.ma_mon_hoc);
        TextView tenMonHoc = view.findViewById(R.id.ten_mon_hoc);
        TextView dvht = view.findViewById(R.id.dvht);
        TextView coPhep = view.findViewById(R.id.co_phep);
        TextView khongPhep = view.findViewById(R.id.khong_phep);
        try {
            maMonHoc.setText(jsonObject.getString("ma_mon_hoc"));
            tenMonHoc.setText(jsonObject.getString("ten_mon_hoc"));
            dvht.setText(jsonObject.getString("dvht"));
            coPhep.setText(jsonObject.getString("nghi_co_phep")+" buổi");
            khongPhep.setText(jsonObject.getString("nghi_ko_phep")+" buổi");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Dialog simpleDialog = DialogUtils.createSimpleDialog(getContext(), view, true);
        if (simpleDialog != null && !simpleDialog.isShowing()) {
            simpleDialog.show();
        }
    }
    public int getScreenWidthInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int widthInDP = Math.round(dm.widthPixels / dm.density);
        return widthInDP;
    }
}
