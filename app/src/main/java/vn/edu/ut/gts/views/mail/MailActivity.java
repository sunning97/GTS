package vn.edu.ut.gts.views.mail;

import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.presenters.mail.MailActivityPresenter;
import vn.edu.ut.gts.views.mail.fragments.MailDetailFragment;
import vn.edu.ut.gts.views.mail.fragments.OnItemClickListener;
import vn.edu.ut.gts.views.mail.fragments.ReceiveListMailFragment;

public class MailActivity extends AppCompatActivity implements IMailActivity,NavigationView.OnNavigationItemSelectedListener,OnItemClickListener {
    @BindView(R.id.mail_toolbar)
    Toolbar mailToolbar;
    @BindView(R.id.mail_drawerlayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.mail_navigation_view)
    NavigationView navigationView;
    @BindView(R.id.student_name)
    TextView studentFullName;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.student_id)
    TextView studentID;

    private MailActivityPresenter mailActivityPresenter;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Storage storage;
    private  ReceiveListMailFragment receiveListMailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        ButterKnife.bind(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        receiveListMailFragment = new ReceiveListMailFragment(this);
        this.storage = new Storage(MailActivity.this);
        setSupportActionBar(mailToolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        profileImage.setImageBitmap(storage.getImageFromStorage(MailActivity.this,"student_portrait.jpg"));

        try {
            JSONObject studentInfo = new JSONObject(storage.getString("student_info"));
            studentFullName.setText(studentInfo.getString("student_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        studentID.setText(storage.getString("last_student_login"));

        getSupportFragmentManager().beginTransaction().replace(
                R.id.mail_fragment_container,
                receiveListMailFragment
        ).commit();
        setTitle("Tin nội bộ sinh viên");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position,JSONObject data) {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.mail_fragment_container,
                new MailDetailFragment(data,MailActivity.this)
        ).commit();
        setTitle("");
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mail_fragment_container);
        if(currentFragment instanceof ReceiveListMailFragment){
            super.onBackPressed();
        } else {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.mail_fragment_container,
                    receiveListMailFragment
            ).commit();
            setTitle("Thông tin nội bộ");
        }
    }
}
