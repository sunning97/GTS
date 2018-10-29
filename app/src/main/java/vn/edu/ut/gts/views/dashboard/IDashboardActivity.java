package vn.edu.ut.gts.views.dashboard;

import android.graphics.Bitmap;

public interface IDashboardActivity {
    public void setToolbarTitle(String title);
    public void setStudentPortrait(Bitmap studentPortrait);
    public void showLoadingDialog();
    public void dismisLoadingDialog();
    public void enableAll();
    public void disableAll();
    public void startLoading();
    public void finishLoading();
}
