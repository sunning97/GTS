package vn.edu.ut.gts.views.search;

import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public interface IStudentSearchActivity {
    void showNetworkErrorLayout();
    void showInputValidateEmpty(String s);
    void generateTableSearchResult(ArrayList<JSONObject> jsonObjects);


    void loadToResultLayout(Boolean isNoResult);

    void showNoResultLayout();
}
