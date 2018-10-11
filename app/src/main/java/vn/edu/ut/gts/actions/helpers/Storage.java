package vn.edu.ut.gts.actions.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
    private SharedPreferences sharedPreferences;
    private String fileName = "storage";
    public Storage(Context context){
        this.sharedPreferences = context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
    }

    public boolean putString(String key, String value){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }
    public String getString(String key){
        return this.sharedPreferences.getString(key, null);
    }
    public boolean setCookie(String cookie){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("cookie",cookie);
        return editor.commit();
    }
    public String getCookie(){
        return this.sharedPreferences.getString("cookie","");
    }
}
