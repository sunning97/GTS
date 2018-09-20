package vn.edu.ut.gts.storage;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {
    private Context context;
    private String fileName = "storage";

    public Storage(Context context){
        this.context = context;
    }

    /**
     * Write data into shared preferences file
     *
     * param String sharePreferencesName HashMap<String,String> data
     * return Boolean
     *
     */


    public String getCookie(){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("cookie","");
    }
    public boolean setCookie(String cookie){
        SharedPreferences sharedPreferences = context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cookie", cookie);
        return editor.commit();
    }
    public boolean putString(String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }
    public String getString(String key, String defaultValue){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,defaultValue);
    }
    public Boolean putData(HashMap<String,String> data) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.fileName,Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(Map.Entry<String, String> entry : data.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }

        return editor.commit();
    }

    /**
     * get data shared preferences by key
     *
     * param String sharePreferencesName List<String></> keys
     * return HashMap<String,String>
     *
     */

    public HashMap<String,String> getData(List<String> keys) {
        if(keys.isEmpty() || keys == null) return null;

        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
        HashMap<String,String> result = new HashMap<>();

        for (String key: keys) {
            result.put(key,sharedPreferences.getString(key,"default_value"));
        }

        return result;
    }

    /**
     * Remove data shared preferences by key
     *
     * param String sharePreferencesName String key
     * return Boolean
     *
     */

    public Boolean removeByKey(String key) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(key);
        return editor.commit();
    }

    /**
     * Remove all data shared preferences
     *
     * param String sharePreferencesName
     * return Boolean
     *
     */
    public Boolean removeAll() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        return editor.commit();
    }
}
