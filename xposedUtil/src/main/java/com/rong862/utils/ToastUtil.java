package com.rong862.utils;

import android.widget.Toast;

import static com.rong862.utils.XposedUtil.appContext;

public class ToastUtil {

    public static void showMessage(String text){

        Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show();
    }
}
