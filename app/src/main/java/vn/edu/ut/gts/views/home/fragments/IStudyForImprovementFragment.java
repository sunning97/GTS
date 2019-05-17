package vn.edu.ut.gts.views.home.fragments;

import org.json.JSONArray;

public interface IStudyForImprovementFragment {
    void showLoading();
    void hideLoading();
    void showInternetError();
    void hideInternetError();
    void showNoClassNotify();
    void generateTableSubjectContent(JSONArray data);
    void generateTableClassContent(JSONArray data);
    void showLoadedLayout();
    void hideLoadedLayout();
    void loadingToAllSubject();
    void loadingToInternetError();
    void internetErrorToLoading();
    void allSubjectToLoading();
    void loadingToAllClass();
}
