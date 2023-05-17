package com.rong862.SplashAd.plugin;

import android.content.Intent;
import android.text.TextUtils;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;

public class QqliveHook extends BaseHook {

    private static final String TAG = "【腾讯视频】";

    public QqliveHook() {
    }

    @Override
    public void startHook() {

        log(TAG, "腾讯视频启动...");

        HookByMatchName(TAG,
                Intent.class,
                null,"getBooleanExtra", String.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        if(TextUtils.equals((String)param.args[0], "home_need_splash")){
                            param.setResult(false);
                        }
                    }
        });
    }
}