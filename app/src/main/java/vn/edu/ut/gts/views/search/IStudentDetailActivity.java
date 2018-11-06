package vn.edu.ut.gts.views.search;

import org.json.JSONArray;

public interface IStudentDetailActivity {
    void setStudentDetailData(JSONArray data);
    void showAllComponent();
    void hideAllComponent();
    void showLoadingDialog();
    void hideLoadingDialog();
    void showNoInternetLayout();
    void hideNoInternetLayout();
    void showStudentPortraitDialog(String studentId);
}
