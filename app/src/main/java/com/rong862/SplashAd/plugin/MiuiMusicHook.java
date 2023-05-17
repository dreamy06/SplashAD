package com.rong862.SplashAd.plugin;

import android.app.Activity;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;

public class MiuiMusicHook extends BaseHook{

    private static final String TAG = "【小米音乐】";

    public MiuiMusicHook(){}

    @Override
    public void startHook() {

        log(TAG,"小米音乐启动...");

        HookByMatchName(TAG,
                "com.tencent.qqmusiclite.activity.SplashAdActivity",
                null,"onCreate", android.os.Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        debug(TAG,"SplashAdActivity finish !");
                        ((Activity)param.thisObject).finish();
                    }
        });
    }
}
