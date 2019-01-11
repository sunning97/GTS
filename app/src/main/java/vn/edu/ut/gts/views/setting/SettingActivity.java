package vn.edu.ut.gts.views.setting;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suke.widget.SwitchButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.CheckWeekSchedule;
import vn.edu.ut.gts.helpers.NotifyWeekScheduleService;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.presenters.home.WeekSchedulePresenter;

public class SettingActivity extends AppCompatActivity {
    @BindView(R.id.setting_toolbar)
    Toolbar settingToolbar;
    @BindView(R.id.sb_notify_week_schedule)
    SwitchButton sbNotifyWeekChedule;
    @BindView(R.id.setting_time_week_schedule_notify)
    LinearLayout settingTimeWeekScheduleNotify;
    @BindView(R.id.parent_layout)
    LinearLayout parentLayout;
    @BindView(R.id.week_schedule_notify_item_layout)
    LinearLayout weekScheduleNotifyItemLayout;


    CharSequence[] values = {"Trước 1 giờ", "Trước 2 giờ", "Trước 6 giờ"};
    AlertDialog alertDialog1;
    TextView settingTimeWeekScheduleNotifyTextView1, settingTimeWeekScheduleNotifyTextView2;
    private Storage storage;
    private WeekSchedulePresenter weekSchedulePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        storage = new Storage(this);
        weekSchedulePresenter = new WeekSchedulePresenter(this);
        final Intent intent = new Intent(this,NotifyWeekScheduleService.class);

        settingTimeWeekScheduleNotifyTextView1 = (TextView) settingTimeWeekScheduleNotify.getChildAt(0);
        settingTimeWeekScheduleNotifyTextView2 = (TextView) settingTimeWeekScheduleNotify.getChildAt(1);

        if (storage.getString("week_schedule_notify") != null && Boolean.parseBoolean(storage.getString("week_schedule_notify"))) {
            sbNotifyWeekChedule.setChecked(true);
            settingTimeWeekScheduleNotify.setEnabled(true);
            settingTimeWeekScheduleNotifyTextView1.setTextColor(getResources().getColor(R.color.black));
            settingTimeWeekScheduleNotifyTextView2.setText("Trước " + storage.getString("week_schedule_notify_time") + " giờ");
        } else {
            settingTimeWeekScheduleNotify.setEnabled(false);
            settingTimeWeekScheduleNotifyTextView1.setTextColor(getResources().getColor(R.color.gray2));
            settingTimeWeekScheduleNotifyTextView2.setText(values[0]);
        }

        settingToolbar.setTitle("Cài đặt");
        setSupportActionBar(settingToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sbNotifyWeekChedule.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                settingTimeWeekScheduleNotify.setEnabled(isChecked);
                storage.putString("week_schedule_notify", String.valueOf(isChecked));
                if (!isChecked) {
                    stopService(intent);
                    settingTimeWeekScheduleNotifyTextView1.setTextColor(getResources().getColor(R.color.gray2));

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent1 = new Intent(getApplicationContext(),CheckWeekSchedule.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),1,intent1,0);
                    alarmManager.cancel(pendingIntent);
                } else {
                    if (storage.getString("week_schedule_notify_time") == null)
                        setSettingTimeWeekScheduleNotify(0);
                    weekSchedulePresenter.saveData();
                    startService(intent);
                    settingTimeWeekScheduleNotifyTextView1.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });


        weekScheduleNotifyItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sbNotifyWeekChedule.setChecked(!sbNotifyWeekChedule.isChecked());
            }
        });

        settingTimeWeekScheduleNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialogWithRadioButtonGroup();
            }
        });
    }

    private void CreateAlertDialogWithRadioButtonGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

        builder.setTitle("Chọn thời gian thông báo");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        settingTimeWeekScheduleNotifyTextView2.setText(values[0]);
                        setSettingTimeWeekScheduleNotify(0);
                        break;
                    case 1:
                        settingTimeWeekScheduleNotifyTextView2.setText(values[1]);
                        setSettingTimeWeekScheduleNotify(1);
                        break;
                    case 2:
                        settingTimeWeekScheduleNotifyTextView2.setText(values[2]);
                        setSettingTimeWeekScheduleNotify(2);
                        break;
                }
                alertDialog1.dismiss();
                Snackbar.make(parentLayout, "Thay đổi thành công!", Snackbar.LENGTH_SHORT).show();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    private void setSettingTimeWeekScheduleNotify(int pos) {
        String[] tmp = values[pos].toString().split(" ");
        storage.putString("week_schedule_notify_time", tmp[1]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
