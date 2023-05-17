package com.rong862.SplashAd;

import com.rong862.SplashAd.plugin.BaseHook;
import com.rong862.SplashAd.utils.PackageUtil;
import com.rong862.utils.XposedUtil;

import static com.rong862.utils.LogUtil.error;
import static com.rong862.utils.LogUtil.log;

public class MainHook extends XposedUtil {

    private static final String TAG = "【LoadPackage】";

    static {
        setLogTag("【开屏小能手】");
        setDebug(false);
    }

    @Override
    public void loadPlugin() {

        if(!PackageUtil.PACKAGE_MAP.containsKey(loadPackageName))
            return;

        log(TAG, loadPackageName + " is loading ! !");

        Class<? extends BaseHook> mClass = PackageUtil.PACKAGE_MAP.get(loadPackageName);

        try {
            mClass.newInstance().startHook();
        } catch (IllegalAccessException | InstantiationException e) {
            error(TAG, "newInstance error: " + e.toString());
        }
    }
}
