package vn.edu.ut.gts.views.dashboard;

import android.graphics.Bitmap;

public interface IDashboardActivity {
    void setToolbarTitle(String title);

    void setStudentPortrait(Bitmap studentPortrait);

    void showLoadingDialog();

    void dismisLoadingDialog();

    void enableAll();

    void disableAll();

    void showErrorDialog();

    void showTimeOutDialog();

    void disableSwipeRefresh();

    void enableSwipeRefresh();

    void resetLoaderImage();

    void resetLoaderTextView();

    void hideLoaderTextView();

    void showLoaderTextView();
}
