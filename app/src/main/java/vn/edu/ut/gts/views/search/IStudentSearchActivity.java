package vn.edu.ut.gts.views.search;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public interface IStudentSearchActivity {
    void showNetworkErrorLayout();
    void showInputValidateEmpty(String s);
    void generateTableSearchResult(ArrayList<JSONObject> jsonObjects);


    void loadToResultLayout(Boolean isNoResult);
    void loadToDetailLayout(String name);
    void showNoResultLayout();

    void setStudentDetailData(JSONArray data);
}
