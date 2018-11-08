package vn.edu.ut.gts.views.mail.fragments;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IReceiveListMailFragment {
    void setupData(JSONArray data);
    void hideAllComponent();
    void showAllComponent();
    void hideLoadingLayout();
    void showLoadingLayout();
}
