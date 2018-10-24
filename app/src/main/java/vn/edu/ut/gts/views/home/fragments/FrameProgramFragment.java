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
    @BindView(R.id.frame_program_table_header)
    TableLayout frameprogramTableHeader;
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
        try {
            JSONArray allQuater = data.getJSONArray("all_quater");
            JSONObject quater = (JSONObject) allQuater.get(position);
            tableLayout.addView(generateSubjectGroup("Học phần bắt buộc ("+quater.getString("so_chi_bat_buoc")+" tín chỉ)"));
            JSONArray batBuoc = quater.getJSONArray("bat_buoc");
            JSONArray khongBatBuoc = quater.getJSONArray("khong_bat_buoc");

            for (int i = 0; i< batBuoc.length(); i++) {
                JSONArray subject = (JSONArray) batBuoc.get(i);
                try {
                    if ((i + 1) % 2 != 0) {
                        tableLayout.addView(generateTableRow(subject, true));
                    } else tableLayout.addView(generateTableRow(subject, false));

                } catch (Exception e) {

                }
            }

            if(khongBatBuoc.length() > 0){
                tableLayout.addView(generateSubjectGroup("Học phần tự chọn ("+quater.getString("so_chi_khong_bat_buoc")+" tín chỉ)"));
                for (int i = 0; i< khongBatBuoc.length(); i++) {
                    JSONArray subject = (JSONArray) khongBatBuoc.get(i);
                    try {
                        if ((i + 1) % 2 != 0) {
                            tableLayout.addView(generateTableRow(subject, true));
                        } else tableLayout.addView(generateTableRow(subject, false));

                    } catch (Exception e) {

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                layoutParams.width = (int) (getScreenWidthInDPs(getContext())*0.6);
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
    private TableRow generateTableRow(JSONArray jsonArray,boolean changeBG){
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setMinimumHeight((int)d*40);
        if(changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray));
        try {
            row.addView(generateTableCell(jsonArray.get(1).toString(),false,(int)(getScreenWidthInDPs(getContext())*0.6)));
            row.addView(generateTableCell(jsonArray.get(5).toString(),true,(int)(getScreenWidthInDPs(getContext())*0.2)));
            row.addView(generateTableCell(jsonArray.get(6).toString(),true,(int)(getScreenWidthInDPs(getContext())*0.2)));
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

    private TableRow generateSubjectGroup(String content){
        TableRow tableRow = new TableRow(getContext());
        tableRow.setGravity(Gravity.CENTER_VERTICAL);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setMinimumHeight((int)d*40);
        tableRow.setBackgroundColor(getResources().getColor(R.color.grandStart));

        // generate cell container
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.START);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        // generate cell's text view
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams a = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        a.setMargins((int)d*10,0,(int)d,0);
        textView.setLayoutParams(a);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView.setTypeface(textView.getTypeface(),Typeface.BOLD);
        textView.setText(content);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.text_size));
        linearLayout.addView(textView);
        tableRow.addView(linearLayout);
        return tableRow;
    }
    private void spinnerInit(){
        try {
            JSONArray allQuater = data.getJSONArray("all_quater");

            for (int i = 0;i< allQuater.length();i++){
                JSONObject jsonObject = (JSONObject) allQuater.get(i);
                dataSpinner.add(jsonObject.getString("quater_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        frameProgramSpinner.setItems(dataSpinner);
        frameprogramTableHeader.addView(this.generateTableHeader());
    }
    public int getScreenWidthInDPs(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
//        int widthInDP = Math.round(dm.widthPixels / dm.density);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }
}
