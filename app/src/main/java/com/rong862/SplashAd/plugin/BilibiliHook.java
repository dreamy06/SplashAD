package com.rong862.SplashAd.plugin;

import android.content.Intent;
import android.text.TextUtils;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;

public class BilibiliHook extends BaseHook{

    private static final String TAG = "【哔哩哔哩】";

    public BilibiliHook(){}

    @Override
    public void startHook() {

        log(TAG,"哔哩哔哩启动...");

        HookByMatchName(TAG,
                Intent.class,
                null,"getAction",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        if(TextUtils.equals((String)param.getResult(), "android.intent.action.MAIN")){
                            param.setResult("");
                        }
                    }
        });
    }
}
