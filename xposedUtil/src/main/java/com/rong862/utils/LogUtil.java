package com.rong862.utils;

import de.robv.android.xposed.XposedBridge;

public class LogUtil {

    private static String TAG = "【Xposed】";
    
    private static boolean isDebug = false;
    
    public static void setLogTag(String logTag) {
    	
    	TAG = logTag;
    }
    
    public static void setDebug(boolean b) {

        isDebug = b;
    }

    public static void log(String pluginTag, String text){

        XposedBridge.log(TAG + pluginTag + text);
    }

    public static void error(String pluginTag, String text){

        XposedBridge.log(TAG + "【Error】" + pluginTag + text);
    }

    public static void debug(String pluginTag, String text){

        if(!isDebug)
            return;

        XposedBridge.log(TAG + pluginTag + text);
    }
}
