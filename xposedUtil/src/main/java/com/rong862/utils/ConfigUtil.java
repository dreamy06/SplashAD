package com.rong862.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class ConfigUtil {

    private final SharedPreferences mSharedPreferences;

    public ConfigUtil(Context context, String preName) {

        mSharedPreferences = context.getSharedPreferences(preName, Context.MODE_PRIVATE);
    }

    public ConfigUtil(Context context, String preName, String providerName) {

        MultiprocessSharedPreferences.setAuthority(providerName);
        mSharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(context, preName, Context.MODE_PRIVATE);
    }

    public boolean getBoolean(String name) {

        return mSharedPreferences.getBoolean(name,false);
    }

    public boolean getBoolean(String name, boolean b) {

        return mSharedPreferences.getBoolean(name,b);
    }

    public void setBoolean(String name, boolean b){

        mSharedPreferences.edit().putBoolean(name, b).apply();
    }

    public String getString(String name) {

        return mSharedPreferences.getString(name,"0");
    }

    public String getString(String name, String def) {

        return mSharedPreferences.getString(name, def);
    }

    public void setString(String name, String msg){

        mSharedPreferences.edit().putString(name, msg).apply();
    }

    public int getInt(String name) {

        return mSharedPreferences.getInt(name,0);
    }

    public int getInt(String name, int def) {

        return mSharedPreferences.getInt(name, def);
    }

    public void setInt(String name, int i){

        mSharedPreferences.edit().putInt(name, i).apply();
    }

}
