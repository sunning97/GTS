package vn.edu.ut.gts.views.mail.fragments;

import org.json.JSONObject;

public interface IMailSentDetailFragment {
    void setMailDetailContent(JSONObject jsonObject);
    void hideAllComponent();
    void showAllComponent();
    void showNoInternetLayout();
    void hideNoInternetLayout();
    void showLoadingDialog();
    void hideLoadingDialog();
}
