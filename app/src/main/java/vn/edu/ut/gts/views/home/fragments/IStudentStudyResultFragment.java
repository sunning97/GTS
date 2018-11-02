package vn.edu.ut.gts.views.home.fragments;

import org.json.JSONObject;

public interface IStudentStudyResultFragment {
    void studyResultDetailShow(JSONObject jsonObject);

    void spinnerInit();

    void generateTableContent(int position);

    void showLoadingDialog();

    void dismissLoadingDialog();

    void setData(JSONObject data);

    void showAllComponent();

    void hideAllComponent();

    void showNetworkErrorLayout();
}
