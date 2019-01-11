package vn.edu.ut.gts.views.home.fragments;

import org.json.JSONArray;

public interface IStudyForImprovementFragment {
    void showLoading();
    void hideLoading();
    void showInternetError();
    void hideInternetError();
    void generateTableContent(JSONArray data);
    void showLoadedLayout();
    void hideLoadedLayout();
    void loadingToLoaded();
    void loadingToInternetError();
    void internetErrorToLoading();
}
