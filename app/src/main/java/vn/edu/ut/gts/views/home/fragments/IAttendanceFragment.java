package vn.edu.ut.gts.views.home.fragments;

import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface IAttendanceFragment {
    public void generateTableContent(JSONArray data);
    public LinearLayout generateTableCell(String content, Boolean isMarginCenter,int width);
    public TableRow generateTableHeader();
    public void attendanceDetailShow(JSONObject jsonObject);
    public void initAttendanceSpiner(List<String> dataSnpinner);
    public void showLoadingDialog();
    public void dismissLoadingDialog();
    public void showTimeoutDialog();
    public void showNoInternetDialog();
    public void showLoadedLayout();
}
