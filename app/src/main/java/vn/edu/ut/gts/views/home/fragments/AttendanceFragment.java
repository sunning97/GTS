package vn.edu.ut.gts.views.home.fragments;


import android.annotation.SuppressLint;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenters.home.AttendanceFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment implements IAttendanceFragment {

    @BindView(R.id.student_total_halt_date)
    TextView tvStudentTotalDaltDate;
    @BindView(R.id.student_attendance_table)
    TableLayout studentAttendanceTable;
    @BindView(R.id.student_attendance_spinner)
    MaterialSpinner studentAttendanceSpinner;
    @BindView(R.id.student_attendance_table_header)
    TableLayout studentAttendanceTableHeader;
    @BindView(R.id.loaded_layout)
    LinearLayout loadedLayout;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.rety_icon)
    AVLoadingIndicatorView retryIcon;
    @BindView(R.id.retry_text)
    TextView retryText;
    @BindView(R.id.total_halt_day_layout)
    LinearLayout totalHaltDayLayout;
    private AttendanceFragmentPresenter attendanceFragmentPresenter;
    private float d;
    private int currentPos = 0;

    List<String> headerText = new ArrayList<>();

    SweetAlertDialog loadingDialog;

//    EpicDialog loadingDialog;

    public AttendanceFragment() {
        headerText.add("Tên môn học");
        headerText.add("ĐVHT");
        headerText.add("Có phép");
        headerText.add("Ko phép");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        ButterKnife.bind(this, view);
        this.attendanceFragmentPresenter = new AttendanceFragmentPresenter(this, getContext());
        this.init();
        d = getContext().getResources().getDisplayMetrics().density;
        attendanceFragmentPresenter.getDataAttendanceSpinner();
        return view;
    }


    @OnClick(R.id.retry_text)
    public void retry(View view) {
        retryIcon.smoothToShow();
        retryText.setVisibility(View.GONE);
        attendanceFragmentPresenter.getDataAttendance(currentPos);
    }

    @Override
    public void generateTableContent(JSONArray data) {
        studentAttendanceTable.removeAllViews();
        studentAttendanceTable.addView(this.generateTableHeader());
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject subject = data.getJSONObject(i);
                studentAttendanceTable.addView(generateTableRow(subject, ((i + 1) % 2 == 0)));
            }
            tvStudentTotalDaltDate.setText(String.valueOf(getTotalHaltDate(data)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public TableRow generateTableRow(final JSONObject jsonObject, boolean changeBG) {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setMinimumHeight((int) d * 50);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendanceDetailShow(jsonObject);
            }
        });
        if (changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray));
        try {
            row.addView(generateTableCell(jsonObject.getString("ten_mon_hoc"), false, (int) (getScreenWidthInDPs(getContext()) * 0.4)));
            row.addView(generateTableCell(jsonObject.getString("dvht"), true, (int) (getScreenWidthInDPs(getContext()) * 0.2)));
            row.addView(generateTableCell(jsonObject.getString("nghi_co_phep"), true, (int) (getScreenWidthInDPs(getContext()) * 0.2)));
            row.addView(generateTableCell(jsonObject.getString("nghi_ko_phep"), true, (int) (getScreenWidthInDPs(getContext()) * 0.2)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
    }

    @Override
    public LinearLayout generateTableCell(String content, Boolean isMarginCenter, int width) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        layoutParams.width = width;
        linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 15, (int) d * 5);
        if (isMarginCenter) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (isMarginCenter) textViewLayout.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewLayout);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(content);
        linearLayout.addView(textView);

        return linearLayout;
    }

    @Override
    public TableRow generateTableHeader() {
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

    private void init() {
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);
        retryIcon.smoothToHide();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void attendanceDetailShow(JSONObject jsonObject) {
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
            coPhep.setText(jsonObject.getString("nghi_co_phep") + " buổi");
            khongPhep.setText(jsonObject.getString("nghi_ko_phep") + " buổi");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Dialog simpleDialog = DialogUtils.createSimpleDialog(getContext(), view, true);
        if (simpleDialog != null && !simpleDialog.isShowing()) {
            simpleDialog.show();
        }
    }

    @Override
    public void initAttendanceSpiner(List<String> dataSnpinner) {
        studentAttendanceSpinner.setItems(dataSnpinner);
        studentAttendanceSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                currentPos = position;
                attendanceFragmentPresenter.getDataAttendance(position);
            }
        });
    }

    @Override
    public void showLoadingDialog() {
        if (!loadingDialog.isShowing())
            this.loadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        if (loadingDialog.isShowing())
            this.loadingDialog.dismiss();
    }

    @Override
    public void showTimeoutDialog() {
        if (loadingDialog.isShowing()) loadingDialog.dismiss();
        new SweetAlertDialog(getContext())
                .setTitleText(getResources().getString(R.string.login_error_dialog_title))
                .setContentText(getResources().getString(R.string.login_error_dialog_content))
                .show();
        retryIcon.hide();
        retryText.setVisibility(View.VISIBLE);
        loadedLayout.setVisibility(View.GONE);
        totalHaltDayLayout.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoInternetDialog() {
        if (loadingDialog.isShowing()) loadingDialog.dismiss();
        new SweetAlertDialog(getContext())
                .setTitleText(getResources().getString(R.string.no_internet_access_title))
                .setContentText(getResources().getString(R.string.no_internet_access_content))
                .show();
        retryIcon.hide();
        retryText.setVisibility(View.VISIBLE);
        loadedLayout.setVisibility(View.GONE);
        totalHaltDayLayout.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadedLayout() {
        totalHaltDayLayout.setVisibility(View.VISIBLE);
        loadedLayout.setVisibility(View.VISIBLE);
        retryIcon.hide();
        retryText.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
    }

    public int getTotalHaltDate(JSONArray data) {
        int result = 0;
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject subject = data.getJSONObject(i);
                if (Integer.parseInt(subject.getString("nghi_co_phep")) > 0)
                    result += Integer.parseInt(subject.getString("nghi_co_phep"));
                if (Integer.parseInt(subject.getString("nghi_ko_phep")) > 0)
                    result += Integer.parseInt(subject.getString("nghi_co_phep"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public int getScreenWidthInDPs(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }
}
