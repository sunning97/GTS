package vn.edu.ut.gts.views.home.fragments;


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

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment {

    @BindView(R.id.student_attendance_table)
    TableLayout studentAttendanceTable;
    @BindView(R.id.student_attendance_spinner)
    MaterialSpinner studentAttendanceSpinner;

    List<JSONObject> data = new ArrayList<>();
    String[] dataSnpinner = {"Học kỳ 1 năm học 2018-2019","Học kỳ hè năm học 2017-2018","Học kỳ 2 năm học 2017-2018","Học kỳ 1 năm học 2017-2018","Học kỳ hè năm học 2016-2017","Học kỳ 2 năm học 2016-2017","Học kỳ 1 năm học 2016-2017","Học kỳ hè năm học 2015-2016","Học kỳ 2 năm học 2015-2016","Học kỳ 1 năm học 2015-2016"};

    public AttendanceFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataInit();
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        ButterKnife.bind(this,view);

        studentAttendanceSpinner.setItems(dataSnpinner);
        studentAttendanceSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        this.generateTableContent(studentAttendanceTable,data);
        return  view;
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
                tableRow.addView(generateTableCell(jsonObject.getString("subject_name"),false));
                tableRow.addView(generateTableCell(jsonObject.getString("dhtv"),true));
                tableRow.addView(generateTableCell(jsonObject.getString("halt_permit"),true));
                tableRow.addView(generateTableCell(jsonObject.getString("halt_no_permit"),true));
            } catch (Exception e){

            }
            tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            index++;
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
        try {
            JSONObject obj1 = new JSONObject();
            obj1.put("subject_id", "0101125001");
            obj1.put("subject_name", "Hệ điều hành");
            obj1.put("dhtv", "3");
            obj1.put("halt_permit", "0");
            obj1.put("halt_no_permit", "0");

            data.add(obj1);

            JSONObject obj2 = new JSONObject();
            obj2.put("subject_id", "0101125009");
            obj2.put("subject_name", "Hệ thống thông tin di động tích hợp");
            obj2.put("dhtv", "3");
            obj2.put("halt_permit", "0");
            obj2.put("halt_no_permit", "0");

            data.add(obj2);

            JSONObject obj3 = new JSONObject();
            obj3.put("subject_id", "0101125011");
            obj3.put("subject_name", "Hệ thống viễn thông thế hệ mới");
            obj3.put("dhtv", "3");
            obj3.put("halt_permit", "0");
            obj3.put("halt_no_permit", "0");

            data.add(obj3);

            JSONObject obj4 = new JSONObject();
            obj4.put("subject_id", "0101125010");
            obj4.put("subject_name", "Kỹ thuật định tuyến");
            obj4.put("dhtv", "3");
            obj4.put("halt_permit", "0");
            obj4.put("halt_no_permit", "0");

            data.add(obj4);

            JSONObject obj5 = new JSONObject();
            obj5.put("subject_id", "0101123013");
            obj5.put("subject_name", "Lập trình mạng");
            obj5.put("dhtv", "3");
            obj5.put("halt_permit", "0");
            obj5.put("halt_no_permit", "0");

            data.add(obj5);

            JSONObject obj6 = new JSONObject();
            obj6.put("subject_id", "0101122034");
            obj6.put("subject_name", "Lập trình thiết bị di động");
            obj6.put("dhtv", "2");
            obj6.put("halt_permit", "0");
            obj6.put("halt_no_permit", "0");

            data.add(obj6);

            JSONObject obj7 = new JSONObject();
            obj7.put("subject_id", "0101125008");
            obj7.put("subject_name", "Mô phỏng hệ thống truyền thông");
            obj7.put("dhtv", "3");
            obj7.put("halt_permit", "0");
            obj7.put("halt_no_permit", "0");
;
            data.add(obj7);

            JSONObject obj8= new JSONObject();
            obj8.put("subject_id", "0101123035");
            obj8.put("subject_name", "Thực tập chuyên môn");
            obj8.put("dhtv", "2");
            obj8.put("halt_permit", "0");
            obj8.put("halt_no_permit", "0");

            data.add(obj8);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
