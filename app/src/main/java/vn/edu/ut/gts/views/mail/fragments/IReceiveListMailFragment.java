package vn.edu.ut.gts.views.mail.fragments;

import org.json.JSONArray;

public interface IReceiveListMailFragment {
    void setupData(JSONArray data);
    void hideAllComponent();
    void showAllComponent();
    void hideLoadingLayout();
    void showLoadingLayout();
    void showLoadingDialog();
    void dismissLoadingDialog();
    void updateDataListMail(JSONArray data);
    void updateDataAfterDelete(int position);
    void showLoadingInMailActivity();
    void dismissLoadingInMailActivity();
}
