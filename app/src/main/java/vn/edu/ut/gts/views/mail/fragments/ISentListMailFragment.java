package vn.edu.ut.gts.views.mail.fragments;

import org.json.JSONArray;

public interface ISentListMailFragment {
    void setupData(JSONArray data);
    void hideAllComponent();
    void showAllComponent();
    void hideLoadingLayout();
    void showLoadingLayout();
    void showLoadingDialog();
    void dismissLoadingDialog();
    void showNoInternetLayout();
    void hideNoInternetLayout();
}
