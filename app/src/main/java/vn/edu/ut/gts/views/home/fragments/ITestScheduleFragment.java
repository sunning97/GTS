package vn.edu.ut.gts.views.home.fragments;

import org.json.JSONArray;

public interface ITestScheduleFragment {
    void showLoadingDialog();
    void dismissLoadingDialog();
    void hideAllComponent();
    void showAllComponent();
    void showNoInternetLayout();
    void hideNoInternetLayout();
    void generateTableContent(JSONArray data);
    void setupDataSpiner(JSONArray data);
}
