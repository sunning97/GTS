package vn.edu.ut.gts.views.home.fragments;

import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IFrameProgramFragment {
    void frameDetailShow(JSONArray jsonArray);
    void spinnerInit();
    TableRow generateSubjectGroup(String content);
    LinearLayout generateTableCell(String data, boolean center, int width);
    TableRow generateTableRow(final JSONArray jsonArray, boolean changeBG);
    TableRow generateTableHeader();
    void generateTableContent(int position);
    public void showLoadingDialog();
    public void dismissLoadingDialog();
    public void setData(JSONObject data);
}
