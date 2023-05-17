package com.rong862.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import static com.rong862.utils.LogUtil.error;

public class VersionUtil {

    private static final String TAG = "【VersionUtil】";

    public static String getVersionCode(Context mContext) {

        try {

            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packeInfo = packageManager.getPackageInfo(mContext.getPackageName(),0);
            return String.valueOf(packeInfo.getLongVersionCode());
        } catch (Exception e) {

            error(TAG, "getVersionCode error: " + e);
            return "0";
        }
    }
}
