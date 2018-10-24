package vn.edu.ut.gts.views.home.fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.helpers.EpicDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrameProgramFragment extends Fragment {
    @BindView(R.id.frame_program_table)
    TableLayout frameProgramTable;
    @BindView(R.id.frame_program_spinner)
    MaterialSpinner frameProgramSpinner;

    private Student student;
    private EpicDialog epicDialog;
    private JSONObject data;
    private SweetAlertDialog loadingDialog;
    private float d;
    private List<String> headerText = new ArrayList<>();
    private List<String> dataSpinner = new ArrayList<>();

    public FrameProgramFragment() {
        headerText.add("Tên môn học");
        headerText.add("Số tín chỉ");
        headerText.add("Số tiết");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_frame_program_toolbar_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        student = new Student(getContext());
        View view = inflater.inflate(R.layout.fragment_frame_program, container, false);
        ButterKnife.bind(this,view);
        init();
        d = getContext().getResources().getDisplayMetrics().density;
        setHasOptionsMenu(true);
        getDataFrameProgram();
        frameProgramSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                generateTableContent(frameProgramTable,position);
            }
        });
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
                spinnerInit();
                generateTableContent(frameProgramTable,0);
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
                    for (int i = 0;i <= parts.length-3;i++){
                        if(i == parts.length-3){
                            title1+=parts[i].trim();
                            break;
                        }
                        title1+=parts[i].trim()+" - ";
                    }
                    epicDialog.showFrameProgramInfoDialog(title1.trim(),parts[parts.length-2].trim()+" - "+parts[parts.length-1].trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return true;
    }

    private void generateTableContent(TableLayout tableLayout,int position){
        tableLayout.removeAllViews();
        tableLayout.addView(this.generateTableHeader());
        try {
            JSONArray allQuater = data.getJSONArray("all_quater");
            JSONObject quater = (JSONObject) allQuater.get(position);
            tableLayout.addView(generateSubjectGroup("Học phần bắt buộc ("+quater.getString("so_chi_bat_buoc")+" tín chỉ)"));
            JSONArray batBuoc = quater.getJSONArray("bat_buoc");
            JSONArray khongBatBuoc = quater.getJSONArray("khong_bat_buoc");
            for (int i = 0; i< batBuoc.length(); i++) {
                JSONArray subject = (JSONArray) batBuoc.get(i);
                TableRow tableRow = new TableRow(getContext());
                tableRow.setGravity(Gravity.CENTER);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tableRow.setMinimumHeight((int)d*50);

                if((i+1) % 2 != 0){
                    tableRow.setBackgroundColor(getResources().getColor(R.color.gray));
                }
                try {
                    tableRow.addView(generateTableCell(subject.get(1).toString(),false, (int) (getScreenWidthInDPs(getContext())*0.4)));
                    tableRow.addView(generateTableCell(subject.get(4).toString(),true,(int) (getScreenWidthInDPs(getContext())*0.3)));
                    tableRow.addView(generateTableCell(subject.get(5).toString(),true,(int) (getScreenWidthInDPs(getContext())*0.3)));
                } catch (Exception e){

                }
                tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
            if(khongBatBuoc.length() > 0){
                tableLayout.addView(generateSubjectGroup("Học phần tự chọn ("+quater.getString("so_chi_khong_bat_buoc")+" tín chỉ)"));
                for (int i = 0; i< khongBatBuoc.length(); i++) {
                    JSONArray subject = (JSONArray) khongBatBuoc.get(i);
                    TableRow tableRow = new TableRow(getContext());
                    tableRow.setGravity(Gravity.CENTER);
                    tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                    tableRow.setMinimumHeight((int)d*50);
                    tableRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    if((i+1) % 2 != 0){
                        tableRow.setBackgroundColor(getResources().getColor(R.color.gray));
                    }
                    try {
                        tableRow.addView(generateTableCell(subject.get(1).toString(),false, (int) (getScreenWidthInDPs(getContext())*0.4)));
                        tableRow.addView(generateTableCell(subject.get(4).toString(),true,(int) (getScreenWidthInDPs(getContext())*0.3)));
                        tableRow.addView(generateTableCell(subject.get(5).toString(),true,(int) (getScreenWidthInDPs(getContext())*0.3)));
                    } catch (Exception e){

                    }
                    tableLayout.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private TableRow generateTableHeader(){
        TableRow header = new TableRow(getContext());
        header.setGravity(Gravity.CENTER);
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setMinimumHeight((int)d*50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0;i < headerText.size();i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            if(i == 0) layoutParams.gravity = Gravity.CENTER_VERTICAL; else layoutParams.gravity = Gravity.CENTER;
            if(i == headerText.size()-1) layoutParams.setMargins(0,0,0,0); else layoutParams.setMargins(0,0,0,0);
            linearLayout.setPadding((int)d*10,(int)d*15,(int) d*10,0);
            linearLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(textViewLayout);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTypeface(textView.getTypeface(),Typeface.BOLD);
            textView.setText(headerText.get(i));
            linearLayout.addView(textView);
            header.addView(linearLayout);
        }

        return  header;
    }
    private LinearLayout generateTableCell(String content,Boolean isGravityCenter,int width){
        // generate cell container
        LinearLayout linearLayout = new LinearLayout(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        if(isGravityCenter) layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = width;
        linearLayout.setLayoutParams(layoutParams);

        // generate cell's text view
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textLayoutParams.setMargins((int)d*10,0,(int)d*5,0);
        textView.setLayoutParams(textLayoutParams);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(content);
        linearLayout.addView(textView);
        return linearLayout;
    }
    private TableRow generateSubjectGroup(String content){
        TableRow tableRow = new TableRow(getContext());
        tableRow.setGravity(Gravity.CENTER_VERTICAL);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
        tableRow.setMinimumHeight((int)d*50);
        tableRow.setBackgroundColor(getResources().getColor(R.color.grandStart));

        // generate cell container
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.START);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        // generate cell's text view
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams a = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        a.setMargins((int)d*20,0,(int)d,0);
        textView.setLayoutParams(a);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView.setTypeface(textView.getTypeface(),Typeface.BOLD);
        textView.setText(content);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.textsize));
        linearLayout.addView(textView);
        tableRow.addView(linearLayout);
        return tableRow;
    }
    private void spinnerInit(){
        try {
            JSONArray allQuater = data.getJSONArray("all_quater");

            for (int i = 0;i< allQuater.length();i++){
                JSONObject jsonObject = (JSONObject) allQuater.get(i);
                int total = Integer.parseInt(jsonObject.getString("so_chi_bat_buoc"))+Integer.parseInt(jsonObject.getString("so_chi_khong_bat_buoc"));
                dataSpinner.add(jsonObject.getString("quater_name")+" ("+total+" tín chỉ)");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        frameProgramSpinner.setItems(dataSpinner);
    }
    public int getScreenWidthInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int widthInDP = Math.round(dm.widthPixels / dm.density);
        return widthInDP;
    }
}
