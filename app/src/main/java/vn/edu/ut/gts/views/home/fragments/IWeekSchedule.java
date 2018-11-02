package vn.edu.ut.gts.views.home.fragments;

import org.json.JSONArray;

public interface IWeekSchedule {
    void showLoadingDialog();
    void dismissLoadingDialog();
    void setDateToDate(JSONArray jsonArray);
    void modifyDataOnfirst(JSONArray jsonArray);
    void modifyDataChange(JSONArray jsonArray);
    void hideAllComponent();
    void showAllComponent();
    void showNetworkErrorLayout();
}
