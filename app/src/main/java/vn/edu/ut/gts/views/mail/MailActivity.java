package vn.edu.ut.gts.views.mail;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenters.mail.MailActivityPresenter;
import vn.edu.ut.gts.views.mail.fragments.MailDetailFragment;
import vn.edu.ut.gts.views.mail.fragments.OnDeleteSuccess;
import vn.edu.ut.gts.views.mail.fragments.OnItemClickListener;
import vn.edu.ut.gts.views.mail.fragments.OnMailDeleteClick;
import vn.edu.ut.gts.views.mail.fragments.ReceiveListMailFragment;

public class MailActivity extends AppCompatActivity implements IMailActivity,NavigationView.OnNavigationItemSelectedListener,OnItemClickListener,OnMailDeleteClick,OnDeleteSuccess {
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
    private AlertDialog alertDialog;
    private EpicDialog epicDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        ButterKnife.bind(this);
        epicDialog = new EpicDialog(this);
        epicDialog.initLoadingDialog();

        receiveListMailFragment = new ReceiveListMailFragment(this,this,this);
        this.storage = new Storage(MailActivity.this);
        setSupportActionBar(mailToolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.receive_mail);
        menuItem.setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawers();
        switch (item.getItemId()){
            case R.id.receive_mail:{
                item.setChecked(true);
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mail_fragment_container);
                if(currentFragment instanceof ReceiveListMailFragment) break;
                else {
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.mail_fragment_container,
                            receiveListMailFragment
                    ).commit();
                    setTitle("Thông tin nội bộ");
                }
                break;
            }
            case R.id.sent_mail:{
                item.setChecked(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Oops...");
                builder.setMessage("Chức năng đang trong quá trình phát triển. Sẽ hoàn thiện sớm trong tương lai :)");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position,JSONObject data) {
        TextView textView = view.findViewById(R.id.mail_circle);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.mail_fragment_container,
                new MailDetailFragment(this,data,this,textView.getBackground(),position)
        ).commit();
        setTitle("");
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mail_fragment_container);
        if(currentFragment instanceof ReceiveListMailFragment){
            super.onBackPressed();
        } else {
            Menu menu = navigationView.getMenu();
            MenuItem menuItem = menu.findItem(R.id.receive_mail);
            menuItem.setChecked(true);
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.mail_fragment_container,
                    receiveListMailFragment
            ).commit();
            setTitle("Thông tin nội bộ");
        }
    }

    @Override
    public void onClickDelete(int position) {
        receiveListMailFragment.deleteAt(position);
    }

    @Override
    public void onDeleteSuccess() {
        onBackPressed();
        View parentLayout = findViewById(R.id.mail_fragment_container);
        Snackbar.make(parentLayout,"Đã xóa!",Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialog() {
        epicDialog.showLoadingDialog();
    }

    @Override
    public void dismissLoadingDialog() {
        epicDialog.dismisPopup();
    }

    @Override
    public void showDeleteFailDialog() {
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.delete_mail_failed_title))
                .setContentText(getResources().getString(R.string.delete_mail_failed_content))
                .show();
    }

}
