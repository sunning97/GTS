package vn.edu.ut.gts.views.search;

import org.json.JSONArray;

public interface IStudentDetailActivity {
    void setStudentDetailData(JSONArray data);
    void showAllComponent();
    void hideAllComponent();
    void showLoadingLayout();
    void hideLoadingLayout();
    void showNoInternetLayout();
    void hideNoInternetLayout();
}
