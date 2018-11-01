package vn.edu.ut.gts.views.home.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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

import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.viethoa.DialogUtils;
import com.wang.avi.AVLoadingIndicatorView;

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
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.presenters.home.StudentDebtFragmentPresenter;
import vn.edu.ut.gts.presenters.home.StudentStudyResultFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentDebtFragment extends Fragment implements IStudentDebtFragment {

    @BindView(R.id.student_debt_table)
    TableLayout studentDebtTable;
    @BindView(R.id.student_debt_spinner)
    MaterialSpinner studentDebtSpinner;
    @BindView(R.id.student_total_debt)
    TextView studentTotalDebt;
    @BindView(R.id.student_debt_table_header)
    TableLayout studentDebtTableHeader;
    @BindView(R.id.semester_select_tv)
    TextView semesterSelectTV;
    @BindView(R.id.total_debt_layout)
    LinearLayout totalDebtLayout;
    @BindView(R.id.loaded_layout)
    LinearLayout loadedLayout;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.rety_icon)
    AVLoadingIndicatorView retyIcon;
    @BindView(R.id.retry_text)
    TextView retryText;


    public static int currentPos = 0;
    private StudentDebtFragmentPresenter studentDebtFragmentPresenter;
    private float d;
    SweetAlertDialog loadingDialog;
    private List<String> headerText = new ArrayList<>();
    private List<String> spinnerData = new ArrayList<>();

    public StudentDebtFragment() {
        headerText.add("Nội dung thu");
        headerText.add("Tín chỉ");
        headerText.add("Công nợ");
        headerText.add("Trạng thái");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_debt, container, false);
        ButterKnife.bind(this, view);
        studentDebtFragmentPresenter = new StudentDebtFragmentPresenter(this, getContext());
        init();
        d = getContext().getResources().getDisplayMetrics().density;
        StudentDebtFragmentPresenter.currentStatus = 0;
        studentDebtFragmentPresenter.getDataDebtSpinner();
        return view;
    }


    @OnClick(R.id.retry_text)
    public void rety(View view){
        StudentDebtFragmentPresenter.currentStatus = 0;
        studentDebtFragmentPresenter.getDataDebtSpinner();
    }

    @Override
    public void generateTableContent(JSONArray data) {
        studentDebtTable.removeAllViews();
        studentDebtTableHeader.removeAllViews();
        studentDebtTableHeader.addView(this.generateTableHeader());
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject subject = data.getJSONObject(i);
                studentDebtTable.addView(generateTableRow(subject, (i % 2 == 0)));
            }
            studentTotalDebt.setText(numberFormat(String.valueOf(getTotalDeb(data))));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public TableRow generateTableRow(final JSONObject jsonObject, boolean changeBG) {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setMinimumHeight((int) d * 60);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debtDetailShow(jsonObject);
            }
        });
        if (changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray));
        try {
            row.addView(generateTableCell(jsonObject.getString("noi_dung_thu"), false, jsonObject.getString("trang_thai").equals("Chưa nộp"), (int) (getScreenWidthInDPs(getContext()) * 0.4)));
            row.addView(generateTableCell(jsonObject.getString("tin_chi"), true, jsonObject.getString("trang_thai").equals("Chưa nộp"), (int) (getScreenWidthInDPs(getContext()) * 0.2)));
            row.addView(generateTableCell(jsonObject.getString("cong_no_vnd"), true, jsonObject.getString("trang_thai").equals("Chưa nộp"), (int) (getScreenWidthInDPs(getContext()) * 0.2)));
            row.addView(generateTableCell(jsonObject.getString("trang_thai"), true, jsonObject.getString("trang_thai").equals("Chưa nộp"), (int) (getScreenWidthInDPs(getContext()) * 0.2)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
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

    @Override
    public LinearLayout generateTableCell(String content, Boolean isGravityCenter, Boolean isRed, int width) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        layoutParams.width = width;
        linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 15, (int) d * 5);
        if (isGravityCenter) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (isGravityCenter) textViewLayout.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewLayout);
        if (isRed) {
            textView.setTextColor(getResources().getColor(R.color.red));
        } else textView.setTextColor(getResources().getColor(R.color.black));

        textView.setText(content);
        linearLayout.addView(textView);

        return linearLayout;
    }

    private void init() {
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);
    }

    @Override
    public void debtDetailShow(JSONObject jsonObject) {
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
            soTien.setText(jsonObject.getString("so_tien_vnd") + " VNĐ");
            daNop.setText(jsonObject.getString("da_nop_vnd") + " VNĐ");
            khauTru.setText(jsonObject.getString("khau_tru_vnd"));
            congNo.setText(jsonObject.getString("cong_no_vnd") + " VNĐ");
            if (jsonObject.getString("trang_thai").equals("Chưa nộp"))
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

    @Override
    public void showTimeoutDialog() {
        if (loadingDialog.isShowing()) loadingDialog.dismiss();
//        new SweetAlertDialog(getContext())
//                .setTitleText(getResources().getString(R.string.connect_timeout_dialog_title))
//                .setContentText(getResources().getString(R.string.connect_timeout_dialog_content))
//                .show();
        removeAllSpinnerItem();
        hideAllComponent();
        retyIcon.hide();
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoInternetDialog() {
        if (loadingDialog.isShowing()) loadingDialog.dismiss();
//        new SweetAlertDialog(getContext())
//                .setTitleText(getResources().getString(R.string.no_internet_access_title))
//                .setContentText(getResources().getString(R.string.no_internet_access_content))
//                .show();
        removeAllSpinnerItem();
        hideAllComponent();
        retyIcon.hide();
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAllComponent() {
        semesterSelectTV.setVisibility(View.GONE);
        studentDebtSpinner.setVisibility(View.GONE);
        totalDebtLayout.setVisibility(View.GONE);
        loadedLayout.setVisibility(View.GONE);
    }

    @Override
    public void showAllComponent() {
        noInternetLayout.setVisibility(View.GONE);
        retyIcon.hide();
        semesterSelectTV.setVisibility(View.VISIBLE);
        studentDebtSpinner.setEnabled(true);
        studentDebtSpinner.setVisibility(View.VISIBLE);
        totalDebtLayout.setVisibility(View.VISIBLE);
        loadedLayout.setVisibility(View.VISIBLE);
    }

    public int getScreenWidthInDPs(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    @Override
    public void showLoadingDialog() {
        if (!loadingDialog.isShowing())
            loadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    @Override
    public void initAttendanceSpiner(List<String> dataSnpinner) {
        studentDebtSpinner.setItems(dataSnpinner);
        studentDebtSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                StudentDebtFragment.currentPos = position;
                studentDebtFragmentPresenter.getStudentDebt(position);
            }
        });
    }

    private String numberFormat(String num) {
        String result = "";
        List<String> resultArray = new ArrayList<>();
        String temp = "";
        int counter = 0;
        for (int i = num.length() - 1; i >= 0; i--) {
            temp += num.charAt(i);
            counter++;
            if (counter == 3) {
                resultArray.add(temp);
                counter = 0;
                temp = "";
            }
        }
        ;
        if (counter > 0) {
            resultArray.add(temp);
        }

        for (int i = resultArray.size() - 1; i >= 0; i--) {
            String resTemp = resultArray.get(i);
            for (int j = resTemp.length() - 1; j >= 0; j--) {
                result += resTemp.charAt(j);
            }
            ;
            if (i > 0) {
                result += ',';
            }
        }
        ;
        return result;
    }

    private int getTotalDeb(JSONArray data) {
        int result = 0;
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject subject = data.getJSONObject(i);
                if (Integer.parseInt(Helper.toSlug(subject.getString("cong_no_vnd"))) > 0)
                    result += Integer.parseInt(Helper.toSlug(subject.getString("cong_no_vnd")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void removeAllSpinnerItem(){
        studentDebtSpinner.setItems(spinnerData);
        studentDebtSpinner.setEnabled(false);
    }
}
