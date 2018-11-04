package vn.edu.ut.gts.views.search.fragments;

import org.json.JSONArray;

public interface IStudentSearchDebtFragment {
    void showLoadingDialog();

    void dismissLoadingDialog();

    void showNetworkErrorLayout();

    void generateTableContent(JSONArray data);

    void showAllComponent();
}
