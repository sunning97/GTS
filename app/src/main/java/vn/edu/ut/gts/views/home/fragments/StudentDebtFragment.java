package vn.edu.ut.gts.views.home.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentDebtFragment extends Fragment {

    @BindView(R.id.student_debt_table) TableLayout studentDebtTable;
    @BindView(R.id.student_debt_spinner) MaterialSpinner studentDebtSpinner;
    @BindView(R.id.student_total_debt) TextView studentTotalDebt;
    private float d;
    private Student student;
    private Storage storage;
    private JSONArray semesters;
    List<String> dataSnpinner = new ArrayList<>();
    SweetAlertDialog loadingDialog;
    private int totalDeb = 0;
    List<String> headerText = new ArrayList<>();
    public StudentDebtFragment() {
        //headerText.add("Mã môn học");
        headerText.add("Nội dung thu");
        headerText.add("Tín chỉ");
        //headerText.add("Số tiền");
        //.add("Đã nộp");
        //headerText.add("Khấu trừ");
        headerText.add("Công nợ");
        headerText.add("Trạng thái");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_debt, container, false);
        ButterKnife.bind(this,view);
        student = new Student(getContext());
        storage = new Storage(getContext());
        init();
        d = getContext().getResources().getDisplayMetrics().density;

        this.initDebt();
        this.dataInit(0);

        // spinner
        studentDebtSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
               dataInit(position);
            }
        });

        return view;
    }


    private void dataInit(final int pos){
        AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray jsonArray = student.getStudentDebt(pos);
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                generateTableContent(studentDebtTable, jsonArray);
                loadingDialog.dismiss();
            }
        };
        asyncTask.execute();
    }


    private void generateTableContent(TableLayout tableLayout, JSONArray data){
        tableLayout.removeAllViews();
        totalDeb = 0;
        tableLayout.addView(this.generateTableHeader());
        try {
            for (int i = 0; i< data.length(); i++) {

                final JSONObject subject = data.getJSONObject(i);

                TableRow tableRow = new TableRow(getContext());
                tableRow.setGravity(Gravity.CENTER);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                tableRow.setMinimumHeight((int)d*60);
                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        debtDetailShow(subject);
                    }
                });
                if((i+1) % 2 == 0){
                    tableRow.setBackgroundColor(getResources().getColor(R.color.gray));
                }
                try {
                    tableRow.addView(generateTableCell(subject.getString("noi_dung_thu"),false,(subject.getString("trang_thai").equals("Chưa nộp")), (int) (getScreenWidthInDPs(getContext())*0.4)));
                    tableRow.addView(generateTableCell(subject.getString("tin_chi"),true,(subject.getString("trang_thai").equals("Chưa nộp")),(int) (getScreenWidthInDPs(getContext())*0.2)));
                    tableRow.addView(generateTableCell(subject.getString("cong_no_vnd"),true,(subject.getString("trang_thai").equals("Chưa nộp")),(int) (getScreenWidthInDPs(getContext())*0.2)));

                    if(Integer.parseInt(Helper.toSlug(subject.getString("cong_no_vnd"))) > 0)
                        totalDeb+= Integer.parseInt(Helper.toSlug(subject.getString("cong_no_vnd")));
                    tableRow.addView(generateTableCell(subject.getString("trang_thai"),true,(subject.getString("trang_thai").equals("Chưa nộp")),(int) (getScreenWidthInDPs(getContext())*0.2)));
                } catch (Exception e){

                }
                tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                studentTotalDebt.setText(String.valueOf(totalDeb));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TableRow generateTableHeader(){
        TableRow header = new TableRow(getContext());
        header.setGravity(Gravity.CENTER);
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setMinimumHeight((int)d*60);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (String text:headerText) {

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins((int) d,0,(int) d,0);
            linearLayout.setPadding((int)d*5,(int)d*5,(int)d*5,(int)d*5);
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

    private LinearLayout generateTableCell(String content,Boolean isGravityCenter,Boolean isRed,int width){
        // generate cell container
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        if(isGravityCenter) layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = (int) (width*d);
        linearLayout.setLayoutParams(layoutParams);

        // generate cell's text view
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams a = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        a.setMargins((int)d,0,(int)d,0);
        textView.setLayoutParams(a);
        textView.setTextColor(getResources().getColor(R.color.black));
        if(isRed) textView.setTextColor(getResources().getColor(R.color.red));
        textView.setText(content);
        linearLayout.addView(textView);
        return linearLayout;
    }

    private void initDebt(){
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                student.getDataStudentDebt();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONObject dataDebt = new JSONObject(storage.getString("dataDebt"));
                    semesters = new JSONArray(dataDebt.getString("semesters"));
                    for (int i = 0; i < semesters.length(); i++) {
                        JSONObject jsonObject = (JSONObject) semesters.get(i);
                        dataSnpinner.add(jsonObject.getString("text"));
                    }
                } catch (Exception e){}
                studentDebtSpinner.setItems(dataSnpinner);
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
    protected void debtDetailShow(JSONObject jsonObject) {
        String title = "Chi tiết công nợ";
        LayoutInflater factory = getLayoutInflater();
        View view = factory.inflate(R.layout.student_debt_detail_dialog, null);
        TextView maMonHoc = view.findViewById(R.id.ma_mon_hoc);
        TextView noiDungThu = view.findViewById(R.id.noi_dung_thu);
        TextView tinChi = view.findViewById(R.id.tin_chi);
        TextView soTien = view.findViewById(R.id.so_tien);
        TextView daNop = view.findViewById(R.id.da_nop);
        TextView khauTru = view.findViewById(R.id.khau_tru);
        TextView congNo = view.findViewById(R.id.cong_no);
        TextView trangThai = view.findViewById(R.id.trang_thai);

        try {
            maMonHoc.setText(jsonObject.getString("ma"));
            noiDungThu.setText(jsonObject.getString("noi_dung_thu"));
            tinChi.setText(jsonObject.getString("tin_chi"));
            soTien.setText(jsonObject.getString("so_tien_vnd")+" VNĐ");
            daNop.setText(jsonObject.getString("da_nop_vnd")+" VNĐ");
            khauTru.setText(jsonObject.getString("khau_tru_vnd"));
            congNo.setText(jsonObject.getString("cong_no_vnd")+" VNĐ");
            if(jsonObject.getString("trang_thai").equals("Chưa nộp"))
                trangThai.setTextColor(getResources().getColor(R.color.red));
            else trangThai.setTextColor(getResources().getColor(R.color.green));
            trangThai.setText(jsonObject.getString("trang_thai"));

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
