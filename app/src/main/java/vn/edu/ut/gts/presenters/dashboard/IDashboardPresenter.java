package vn.edu.ut.gts.presenters.dashboard;

import org.json.JSONObject;

public interface IDashboardPresenter {
    JSONObject getStudentInfoData();
    void getStudentPortrait();
    void go();
}
