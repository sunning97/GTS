package vn.edu.ut.gts.views.search.fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.views.home.fragments.StudentStudyResultFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentSearchStudyResultFragment extends Fragment {
    @BindView(R.id.study_result_spinner)
    MaterialSpinner studyResultSpinner;
    @BindView(R.id.study_result_table)
    TableLayout studyResultTable;
    @BindView(R.id.study_result_table_header)
    TableLayout studyResultTableHeader;
    @BindView(R.id.loaded_layout)
    LinearLayout loadedLayout;
    @BindView(R.id.semester_select_tv)
    TextView semesterSelectTV;
    private List<String> headerText;
    private List<String> dataSpinner;
    private JSONObject data;
    private float d;
    public StudentSearchStudyResultFragment() {
        dataSpinner = new ArrayList<>();
        headerText = new ArrayList<>();
        headerText.add("Học phần");
        headerText.add("Điểm 10");
        headerText.add("Điểm 4");
        headerText.add("Điểm chữ");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_search_study_result, container, false);
        ButterKnife.bind(this, view);
        d = getContext().getResources().getDisplayMetrics().density;

        Bundle bundle = getArguments();
        try {
            JSONObject data = new JSONObject(bundle.getString("data"));
            this.data = data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        spinnerInit();
        generateTableContent(0);
        showAllComponent();
        return view;
    }


    private TableRow generateTableHeader() {
        TableRow header = new TableRow(getContext());
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setMinimumHeight((int) d * 50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0; i < headerText.size(); i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            if (i == 0) {
                layoutParams.width = (int) (getScreenWidthInDPs(getContext()) * 0.4);
            } else {
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.width = (int) (getScreenWidthInDPs(getContext()) * 0.2);
            }
            linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 5, 0);
            linearLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(textViewLayout);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setText(headerText.get(i));
            linearLayout.addView(textView);
            header.addView(linearLayout);
        }

        return header;
    }

    public void generateTableContent(int position) {
        this.studyResultTable.removeAllViews();
        try {
            JSONArray allSemester = data.getJSONArray("all_semester");
            JSONObject semester = (JSONObject) allSemester.get(position);
            JSONArray subjects = semester.getJSONArray("subjects");
            for (int i = 0; i < subjects.length(); i++) {
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

    private TableRow generateTableRow(final JSONObject data, boolean changeBG) {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setMinimumHeight((int) d * 50);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        if (changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray));
        try {
            row.addView(generateTableCell(data.getString("courseName"), false, (int) (getScreenWidthInDPs(getContext()) * 0.4)));
            row.addView(generateTableCell(data.getString("scoresOf10"), true, (int) (getScreenWidthInDPs(getContext()) * 0.2)));
            row.addView(generateTableCell(data.getString("scoresOf4"), true, (int) (getScreenWidthInDPs(getContext()) * 0.2)));
            row.addView(generateTableCell(data.getString("scoresString"), true, (int) (getScreenWidthInDPs(getContext()) * 0.2)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
    }

    private LinearLayout generateTableCell(String data, boolean center, int width) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        layoutParams.width = width;
        linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 15, (int) d * 5);
        if (center) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (center) textViewLayout.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewLayout);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(data);
        linearLayout.addView(textView);

        return linearLayout;
    }

    public void spinnerInit() {
        try {
            JSONArray allSemester = data.getJSONArray("all_semester");
            for (int i = 0; i < allSemester.length(); i++) {
                JSONObject jsonObject = (JSONObject) allSemester.get(i);
                dataSpinner.add(jsonObject.getString("quater"));
            }
            studyResultSpinner.setItems(dataSpinner);
            studyResultSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    StudentStudyResultFragment.currentPos = position;
                    generateTableContent(position);
                }
            });
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            Log.d("AAA","AAAAAA");
        }catch (JSONException e) {
            e.printStackTrace();
        }

        studyResultTableHeader.addView(this.generateTableHeader());
    }

    public void showAllComponent() {
        semesterSelectTV.setVisibility(View.VISIBLE);
        studyResultSpinner.setVisibility(View.VISIBLE);
        loadedLayout.setVisibility(View.VISIBLE);
    }

    private int getScreenWidthInDPs(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }
}
