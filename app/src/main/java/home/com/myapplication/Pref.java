package home.com.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {

    Context ctx;
    static String ISCONFIGED = "ISCONFIGED";
    static String PASSWORD = "PASSWORD";
    static String USERNAME = "USERNAME";
    static String SITE_PASSWORD = "SITE_PASSWORD";
    static String SITE_USERNAME = "SITE_USERNAME";
    static String SIMNO = "SIMNO";

    public Pref(Context ctx)
    {
        this.ctx = ctx;
    }
    public String PrefGetString(String key)
    {
        SharedPreferences shared = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return shared.getString(key, "");
    }
    public void PrefSetString(String key,String val)
    {
        SharedPreferences shared = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public boolean PrefGetBool(String key)
    {
        SharedPreferences shared = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return shared.getBoolean(key, false);
    }
    public void PrefSetBool(String key,boolean val)
    {
        SharedPreferences shared = ctx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(key, val);
        editor.apply();
    }

}
