package vn.edu.ut.gts.views.mail.fragments;

import org.json.JSONObject;

public interface IMailDetailFragment {
    void setMailDetailContent(JSONObject jsonObject);
    void hideAllComponent();
    void showAllComponent();
}
