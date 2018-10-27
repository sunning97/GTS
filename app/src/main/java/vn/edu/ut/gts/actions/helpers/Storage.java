package vn.edu.ut.gts.actions.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.Connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public boolean saveImage(Connection.Response resultImageResponse, Context context) {
        boolean result = false;
        File image = new File(context.getFilesDir(), "student_portrait.jpg");
        if (image.exists()) image.delete();
        try {
            FileOutputStream fileOutputStream = null;
            fileOutputStream = context.openFileOutput("student_portrait.jpg", Context.MODE_PRIVATE);
            fileOutputStream.write(resultImageResponse.bodyAsBytes());
            fileOutputStream.close();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Bitmap getImageFromStorage(Context context) {
        Bitmap b = null;
        try {
            File f = new File(context.getFilesDir(), "student_portrait.jpg");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }


    public boolean deleteAllsharedPreferences(){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        String id = this.getString("last_student_login");
        editor.clear();
        editor.putString("last_student_login",id);
        return editor.commit();
    }
}
