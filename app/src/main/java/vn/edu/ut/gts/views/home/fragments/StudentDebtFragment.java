package vn.edu.ut.gts.views.home.fragments;


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

    private Student student;
    private Storage storage;
    private JSONArray semesters;
    List<String> dataSnpinner = new ArrayList<>();
    private int totalDeb = 0;
    List<String> headerText = new ArrayList<>();
    public StudentDebtFragment() {
        headerText.add("Mã môn học");
        headerText.add("Nội dung thu");
        headerText.add("Tín chỉ");
        headerText.add("Số tiền");
        headerText.add("Đã nộp");
        headerText.add("Khấu trừ");
        headerText.add("Công nợ");
        headerText.add("Trạng thái");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_debt, container, false);
        ButterKnife.bind(this,view);
        student = new Student(getContext());
        storage = new Storage(getContext());

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
            protected JSONArray doInBackground(Void... voids) {
                JSONArray jsonArray = student.getStudentDebt(pos);
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                generateTableContent(studentDebtTable, jsonArray);
            }
        };
        asyncTask.execute();
    }


    private void generateTableContent(TableLayout tableLayout,JSONArray data){
        tableLayout.removeAllViews();
        totalDeb = 0;
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
                    tableRow.addView(generateTableCell(subject.getString("ma"),false));
                    tableRow.addView(generateTableCell(subject.getString("noi_dung_thu"),false));
                    tableRow.addView(generateTableCell(subject.getString("tin_chi"),true));
                    tableRow.addView(generateTableCell(subject.getString("so_tien_vnd"),true));
                    tableRow.addView(generateTableCell(subject.getString("da_nop_vnd"),true));
                    tableRow.addView(generateTableCell(subject.getString("khau_tru_vnd"),true));
                    tableRow.addView(generateTableCell(subject.getString("cong_no_vnd"),true));

                    if(Integer.parseInt(Helper.toSlug(subject.getString("cong_no_vnd"))) > 0)
                        totalDeb+= Integer.parseInt(Helper.toSlug(subject.getString("cong_no_vnd")));
                    tableRow.addView(generateTableCell(subject.getString("trang_thai"),true));
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

    private LinearLayout generateTableCell(String content,Boolean isGravityCenter){
        // generate cell container
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        if(isGravityCenter) layoutParams.gravity = Gravity.CENTER;
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

}
