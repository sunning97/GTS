package vn.edu.ut.gts.views.register_subject;

import org.json.JSONArray;

public interface IRegisterSubjectActivity {
    void showLoadingLayout();
    void hideLoadingLayout();
    void showInternetErrorLayout();
    IRegisterSubjectActivity hideInternetErrorLayout();
    void showAllSubjectLayout();
    IRegisterSubjectActivity setTextQuarter();
    IRegisterSubjectActivity hideAllSubjectLayout();
    IRegisterSubjectActivity generateTableSubjectContent(JSONArray data);
    void generateTableClassContent(JSONArray data);
    void loadingToAllSubject();
    void loadingToInternetError();
    void internetErrorToLoading();
    void allSubjectToLoading();
    void loadingToAllClass();
    void showNoClassNotify();
    void allClassReturnAllSubject();
}
