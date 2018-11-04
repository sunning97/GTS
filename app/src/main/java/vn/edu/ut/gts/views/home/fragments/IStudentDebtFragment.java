package vn.edu.ut.gts.views.home.fragments;

import android.widget.LinearLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface IStudentDebtFragment {
    void showLoadingDialog();

    void dismissLoadingDialog();

    void initDebtSpinner(List<String> dataSpinner);

    void generateTableContent(JSONArray data);

    TableRow generateTableHeader();

    LinearLayout generateTableCell(String content, Boolean isGravityCenter, Boolean isRed, int width);

    void debtDetailShow(JSONObject jsonObject);

    void showNetworkErrorLayout();

    void hideAllComponent();

    void showAllComponent();
}
