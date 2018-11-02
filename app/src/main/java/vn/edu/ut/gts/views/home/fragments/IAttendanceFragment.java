package vn.edu.ut.gts.views.home.fragments;

import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface IAttendanceFragment {
    void generateTableContent(JSONArray data);

    LinearLayout generateTableCell(String content, Boolean isMarginCenter, int width);

    TableRow generateTableHeader();

    void attendanceDetailShow(JSONObject jsonObject);

    void initAttendanceSpiner(List<String> dataSnpinner);

    void showLoadingDialog();

    void dismissLoadingDialog();

    void showNetworkErrorLayout();

    void showLoadedLayout();

    void hideAllComponent();

    void showAllComponent();
}
