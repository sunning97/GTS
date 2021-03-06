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
    void loadToNoInternetLayout(int from);
    void setStudentDetailData(JSONArray data);
    void showLoadingDialog();
    void dismissLoadingDialog();
    void showTimeoutDialog();
    void showNoInternetDialog();
    void toRetryBtn();
    void toSearchBtn();
    void toLoadingBtn();
    void disableAllInput();
    void enableAllInout();
}
