package vn.edu.ut.gts.views.home.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentDebtFragment extends Fragment {

    @BindView(R.id.student_debt_table) TableLayout studentDebtTable;
    @BindView(R.id.student_debt_spinner) MaterialSpinner studentDebtSpinner;
    @BindView(R.id.student_total_debt) TextView studentTotalDebt;

    List<JSONObject> data = new ArrayList<>();
    String[] dataSnpinner = {"Học kỳ 1 năm học 2018-2019","Học kỳ hè năm học 2017-2018","Học kỳ 2 năm học 2017-2018","Học kỳ 1 năm học 2017-2018","Học kỳ hè năm học 2016-2017","Học kỳ 2 năm học 2016-2017","Học kỳ 1 năm học 2016-2017","Học kỳ hè năm học 2015-2016","Học kỳ 2 năm học 2015-2016","Học kỳ 1 năm học 2015-2016"};

    public StudentDebtFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.dataInit();
        View view = inflater.inflate(R.layout.fragment_student_debt, container, false);
        ButterKnife.bind(this,view);
        final Student student = new Student(getContext());
        // spinner
        studentDebtSpinner.setItems(dataSnpinner);
        studentDebtSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        this.generateTableContent(studentDebtTable,data);
        return view;
    }

    private void generateTableContent(TableLayout tableLayout,List<JSONObject> data){
        int index = 1;
        for (JSONObject jsonObject: data) {
            TableRow tableRow = new TableRow(getContext());
            tableRow.setGravity(Gravity.CENTER);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
            tableRow.setMinimumHeight(120);
            if(index % 2 == 0){
                tableRow.setBackgroundColor(getResources().getColor(R.color.gray));
            }
            try {
                tableRow.addView(generateTableCell(jsonObject.getString("subject_id"),false));
                tableRow.addView(generateTableCell(jsonObject.getString("content"),false));
                tableRow.addView(generateTableCell(jsonObject.getString("credits"),true));
                tableRow.addView(generateTableCell(jsonObject.getString("deposit"),true));
                tableRow.addView(generateTableCell(jsonObject.getString("submitted"),true));
                tableRow.addView(generateTableCell(jsonObject.getString("deduct"),true));
                tableRow.addView(generateTableCell(jsonObject.getString("debt"),true));
                tableRow.addView(generateTableCell(jsonObject.getString("state"),true));
            } catch (Exception e){

            }
            tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            index++;
        }
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

    private void dataInit(){
        try {
            JSONObject obj1 = new JSONObject();
            obj1.put("subject_id", "0101125001");
            obj1.put("content", "Hệ điều hành");
            obj1.put("credits", "3");
            obj1.put("deposit", "900,000");
            obj1.put("submitted", "0");
            obj1.put("deduct", "0");
            obj1.put("debt", "900,000");
            obj1.put("state", "Chưa nộp");
            data.add(obj1);

            JSONObject obj2 = new JSONObject();
            obj2.put("subject_id", "0101125009");
            obj2.put("content", "Hệ thống thông tin di động tích hợp");
            obj2.put("credits", "3");
            obj2.put("deposit", "900,000");
            obj2.put("submitted", "0");
            obj2.put("deduct", "0");
            obj2.put("debt", "900,000");
            obj2.put("state", "Chưa nộp");
            data.add(obj2);

            JSONObject obj3 = new JSONObject();
            obj3.put("subject_id", "0101125011");
            obj3.put("content", "Hệ thống viễn thông thế hệ mới");
            obj3.put("credits", "3");
            obj3.put("deposit", "900,000");
            obj3.put("submitted", "0");
            obj3.put("deduct", "0");
            obj3.put("debt", "900,000");
            obj3.put("state", "Chưa nộp");
            data.add(obj3);

            JSONObject obj4 = new JSONObject();
            obj4.put("subject_id", "0101125010");
            obj4.put("content", "Kỹ thuật định tuyến");
            obj4.put("credits", "3");
            obj4.put("deposit", "900,000");
            obj4.put("submitted", "0");
            obj4.put("deduct", "0");
            obj4.put("debt", "900,000");
            obj4.put("state", "Chưa nộp");
            data.add(obj4);

            JSONObject obj5 = new JSONObject();
            obj5.put("subject_id", "0101123013");
            obj5.put("content", "Lập trình mạng");
            obj5.put("credits", "3");
            obj5.put("deposit", "900,000");
            obj5.put("submitted", "0");
            obj5.put("deduct", "0");
            obj5.put("debt", "900,000");
            obj5.put("state", "Chưa nộp");
            data.add(obj5);

            JSONObject obj6 = new JSONObject();
            obj6.put("subject_id", "0101122034");
            obj6.put("content", "Lập trình thiết bị di động");
            obj6.put("credits", "2");
            obj6.put("deposit", "900,000");
            obj6.put("submitted", "0");
            obj6.put("deduct", "0");
            obj6.put("debt", "600,000");
            obj6.put("state", "Chưa nộp");
            data.add(obj6);

            JSONObject obj7 = new JSONObject();
            obj7.put("subject_id", "0101125008");
            obj7.put("content", "Mô phỏng hệ thống truyền thông");
            obj7.put("credits", "3");
            obj7.put("deposit", "900,000");
            obj7.put("submitted", "0");
            obj7.put("deduct", "0");
            obj7.put("debt", "900,000");
            obj7.put("state", "Chưa nộp");
            data.add(obj7);

            JSONObject obj8= new JSONObject();
            obj8.put("subject_id", "0101123035");
            obj8.put("content", "Thực tập chuyên môn");
            obj8.put("credits", "2");
            obj8.put("deposit", "600,000");
            obj8.put("submitted", "0");
            obj8.put("deduct", "0");
            obj8.put("debt", "900,000");
            obj8.put("state", "Chưa nộp");
            data.add(obj8);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
