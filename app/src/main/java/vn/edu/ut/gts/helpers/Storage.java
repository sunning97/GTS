package vn.edu.ut.gts.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.UncheckedIOException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import vn.edu.ut.gts.views.login.LoginActivity;

public class Storage {
    private SharedPreferences sharedPreferences;
    private String fileName = "storage";

    public Storage(Context context) {
        this.sharedPreferences = context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
    }

    public boolean putString(String key, String value) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getString(String key) {
        return this.sharedPreferences.getString(key, null);
    }

    public boolean setCookie(String cookie) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("cookie", cookie);
        return editor.commit();
    }

    public String getCookie() {
        return this.sharedPreferences.getString("cookie", "");
    }

    public boolean deleteString(String key) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(key);
        return editor.commit();
    }

    public boolean saveImage(Connection.Response resultImageResponse, Context context,String fileName) {
        boolean result = false;
        File image = new File(context.getFilesDir(), fileName);
        if (image.exists()) image.delete();
        try {
            FileOutputStream fileOutputStream = null;
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(resultImageResponse.bodyAsBytes());
            fileOutputStream.close();
            result = true;
        } catch (UncheckedIOException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean deleteImage(Context context,String fileName){
        File image = new File(context.getFilesDir(), fileName);
        if (image.exists()){
            image.delete();
            return true;
        }
        return false;
    }

    public Bitmap getImageFromStorage(Context context,String fileName) {
        Bitmap b = null;
        try {
            File f = new File(context.getFilesDir(), fileName);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    public boolean isImageExist(Context context,String fileName) {
        File image = new File(context.getFilesDir(), fileName);
        return image.exists();
    }

    public boolean deleteAllsharedPreferences(Context context) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();

        Boolean weekScheduleNotify =false;
        String time = null;
        String weekNotifyData = this.getString("week_notify_data");
        String dataW = this.getString("w_dataLogin");
        String cookieW = this.getString("w_cookie");

        if(this.getString("week_schedule_notify") != null){
            weekScheduleNotify = Boolean.parseBoolean(this.getString("week_schedule_notify"));
            time = this.getString("week_schedule_notify_time");
        }


        if (LoginActivity.isAutoLogin && !LoginActivity.isLogout) {
            String id = this.getString("last_student_login");
            String pass = this.getString("password");
            editor.clear();
            if (!TextUtils.isEmpty(id)) {
                editor.putString("last_student_login", id);
            }
            if (!TextUtils.isEmpty(pass)) {
                editor.putString("password", pass);
            }
            editor.putString("is_auto_login", String.valueOf(true));
        }
        if (LoginActivity.isAutoLogin && LoginActivity.isLogout) {
            File image = new File(context.getFilesDir(), "student_portrait.jpg");
            if (image.exists()) image.delete();
            String id = this.getString("last_student_login");
            editor.clear();
            editor.putString("last_student_login", id);
            editor.putString("is_auto_login", String.valueOf(false));
        }

        if (!LoginActivity.isAutoLogin && (!LoginActivity.isLogout || LoginActivity.isLogout)) {
            File image = new File(context.getFilesDir(), "student_portrait.jpg");
            if (image.exists()) image.delete();
            String id = this.getString("last_student_login");
            editor.clear();
            if (!TextUtils.isEmpty(id)) {
                editor.putString("last_student_login", id);
            }
            editor.putString("is_auto_login", String.valueOf(false));
        }

        File image1 = new File(context.getFilesDir(), "search_student_portrait.jpg");
        if (image1.exists()) image1.delete();

        editor.putString("week_schedule_notify",String.valueOf(weekScheduleNotify));
        editor.putString("week_notify_data",weekNotifyData);
        editor.putString("w_dataLogin",dataW);
        editor.putString("w_cookie",cookieW);

        if(time != null) editor.putString("week_schedule_notify_time",time);

        return editor.commit();
    }
}
