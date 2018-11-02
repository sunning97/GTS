package vn.edu.ut.gts.presenters.home;

public interface IWeekSchedulePresenter {
    void getSchedulesGetMethod();

    void getNextSchedulesWeek();

    void getPrevSchedulesWeek();

    void getCurrentSchedulesWeek();

    void getSchedulesByDate(String date);
}
