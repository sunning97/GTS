package vn.edu.ut.gts.views.home.fragments;

import android.widget.LinearLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface IStudentDebtFragment {
    public void showLoadingDialog();

    public void dismissLoadingDialog();

    public void initAttendanceSpiner(List<String> dataSnpinner);

    public void generateTableContent(JSONArray data);

    public TableRow generateTableHeader();

    public LinearLayout generateTableCell(String content, Boolean isGravityCenter, Boolean isRed, int width);

    public void debtDetailShow(JSONObject jsonObject);

    public void showTimeoutDialog();

    public void showNoInternetDialog();

    public void hideAllComponent();

    public void showAllComponent();
}
