package vn.edu.ut.gts.views.home.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.viethoa.DialogUtils;

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
import vn.edu.ut.gts.helpers.EpicDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentStudyResultFragment extends Fragment {
    @BindView(R.id.study_result_spinner)
    MaterialSpinner studyResultSpinner;
    @BindView(R.id.study_result_table)
    TableLayout studyResultTable;
    @BindView(R.id.study_result_table_header)
    TableLayout studyResultTableHeader;

    private Student student;
    private float d;
    private List<String> headerText = new ArrayList<>();
    private List<String> dataSpinner = new ArrayList<>();
    private SweetAlertDialog loadingDialog;
    private EpicDialog epicDialog;
    private JSONObject data;

    public StudentStudyResultFragment() {
        headerText.add("Học phần");
        headerText.add("Điểm 10");
        headerText.add("Điểm 4");
        headerText.add("Điểm chữ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_student_study_result, container, false);
        ButterKnife.bind(this,view);
        student = new Student(getContext());
        d = getContext().getResources().getDisplayMetrics().density;
        init();
        setHasOptionsMenu(true);
        getDataStudyResult();
        return  view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.study_result_program_toolbar_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.infor: {
                try {

                    epicDialog.showStudyResultInfoDialog(
                            data.getString("trung_binh_tich_luy"),
                            data.getString("tong_tin_chi"),
                            data.getString("ti_le_no"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return true;
    }
    private void getDataStudyResult(){
        AsyncTask<Void,Void,JSONObject> getData = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject jsonObject = student.getStudentStudyResult();
                Log.d("AAA",jsonObject.toString());
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                data = jsonObject;
                spinnerInit();
                generateTableContent(0);
                loadingDialog.dismiss();
            }
        };
        getData.execute();

    }
    private void init() {
        epicDialog = new EpicDialog(getContext());
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);
    }
    private TableRow generateTableHeader(){
        TableRow header = new TableRow(getContext());
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setMinimumHeight((int)d*50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0;i < headerText.size();i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            if(i == 0){
                layoutParams.width = (int) (getScreenWidthInDPs(getContext())*0.4);
            } else{
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.width = (int) (getScreenWidthInDPs(getContext())*0.2);
            }
            linearLayout.setPadding((int)d*5,(int)d*15,(int) d*5,0);
            linearLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(textViewLayout);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTypeface(textView.getTypeface(),Typeface.BOLD);
            textView.setText(headerText.get(i));
            linearLayout.addView(textView);
            header.addView(linearLayout);
        }

        return  header;
    }

    private void generateTableContent(int position){
        this.studyResultTable.removeAllViews();
        try {
            JSONArray allSemester = data.getJSONArray("all_semester");
            JSONObject semester = (JSONObject) allSemester.get(position);
            JSONArray subjects = semester.getJSONArray("subjects");
            for (int i = 0; i< subjects.length(); i++) {
                JSONObject subject = (JSONObject) subjects.get(i);
                try {
                    if ((i + 1) % 2 != 0) {
                        studyResultTable.addView(generateTableRow(subject, true));
                    } else studyResultTable.addView(generateTableRow(subject, false));

                } catch (Exception e) {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private TableRow generateTableRow(final JSONObject data, boolean changeBG){
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setMinimumHeight((int)d*40);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyResultDetailShow(data);
                Log.d("AAA",data.toString());
            }
        });
        if(changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray));
        try {
            row.addView(generateTableCell(data.getString("courseName"),false,(int)(getScreenWidthInDPs(getContext())*0.4)));
            row.addView(generateTableCell(data.getString("scoresOf10"),true,(int)(getScreenWidthInDPs(getContext())*0.2)));
            row.addView(generateTableCell(data.getString("scoresOf4"),true,(int)(getScreenWidthInDPs(getContext())*0.2)));
            row.addView(generateTableCell(data.getString("scoresString"),true,(int)(getScreenWidthInDPs(getContext())*0.2)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
    }
    private LinearLayout generateTableCell(String data,boolean center,int width){
        LinearLayout linearLayout = new LinearLayout(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        layoutParams.width = width;
        linearLayout.setPadding((int)d*5,(int)d*10,(int) d*10,(int) d*5);
        if(center) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if(center) textViewLayout.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewLayout);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(data);
        linearLayout.addView(textView);

        return  linearLayout;
    }
    private void spinnerInit(){
        try {
            JSONArray allSemester = data.getJSONArray("all_semester");
            for (int i = 0;i< allSemester.length();i++){
                JSONObject jsonObject = (JSONObject) allSemester.get(i);
                dataSpinner.add(jsonObject.getString("quater"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        studyResultSpinner.setItems(dataSpinner);
        studyResultSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                generateTableContent(position);
            }
        });
        studyResultTableHeader.addView(this.generateTableHeader());
    }
    private int getScreenWidthInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
//        int widthInDP = Math.round(dm.widthPixels / dm.density);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }
    private float getStudentSemesterSverage(JSONArray subjects){
        float a = 0;
        int totalCourseCredits = 0;
        for (int i = 0; i< subjects.length(); i++) {
            JSONObject subject = null;
            try {
                subject = (JSONObject) subjects.get(i);
                int courseCredits = (TextUtils.isEmpty(subject.getString("courseCredits")) ? 0 :Integer.parseInt(subject.getString("courseCredits")));
                float scoresOf4 = (TextUtils.isEmpty(subject.getString("scoresOf4")) ? 0 :Float.parseFloat(subject.getString("scoresOf4")));
                a+=(courseCredits*scoresOf4);
                totalCourseCredits+=Integer.parseInt(subject.getString("courseCredits"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return (a/totalCourseCredits);
    }

    protected void studyResultDetailShow(JSONObject jsonObject) {
        LayoutInflater factory = getLayoutInflater();
        View view = factory.inflate(R.layout.student_study_result_detail_dialog, null);
        TextView maMonHoc = view.findViewById(R.id.ma_mon_hoc);
        TextView hocPhan = view.findViewById(R.id.hoc_phan);
        TextView lopHoc = view.findViewById(R.id.lop_hoc);
        TextView tinChin = view.findViewById(R.id.tc);
        TextView diemQuaTrinh = view.findViewById(R.id.diem_qua_trinh);
        TextView diemKetThuc = view.findViewById(R.id.thi_ket_thuc);
        TextView diemHe10 = view.findViewById(R.id.diem_he_10);
        TextView diemHe4 = view.findViewById(R.id.diem_he_4);
        TextView diemChu = view.findViewById(R.id.diem_chu);
        TextView xepLoai = view.findViewById(R.id.xep_loai);
        TextView ghiChu = view.findViewById(R.id.ghi_chu);

        try {
            maMonHoc.setText(jsonObject.getString("courseCode"));
            hocPhan.setText(jsonObject.getString("courseName"));
            lopHoc.setText(jsonObject.getString("courseClass"));
            tinChin.setText(jsonObject.getString("courseCredits"));
            diemQuaTrinh.setText(jsonObject.getString("processScore"));
            diemKetThuc.setText(jsonObject.getString("testScores"));
            diemHe10.setText(jsonObject.getString("scoresOf10"));
            diemHe4.setText(jsonObject.getString("scoresOf4"));
            diemChu.setText(jsonObject.getString("scoresString"));
            xepLoai.setText(jsonObject.getString("classification"));
            ghiChu.setText(jsonObject.getString("note"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Dialog simpleDialog = DialogUtils.createSimpleDialog(getContext(), view, true);
        if (simpleDialog != null && !simpleDialog.isShowing()) {
            simpleDialog.show();
        }
    }
}
