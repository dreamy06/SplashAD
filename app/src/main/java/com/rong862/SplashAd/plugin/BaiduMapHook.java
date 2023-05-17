package com.rong862.SplashAd.plugin;

import android.content.Intent;
import android.text.TextUtils;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;


public class BaiduMapHook extends BaseHook{

    private static final String TAG = "【百度地图】";

    public BaiduMapHook(){}

    @Override
    public void startHook() {

        log(TAG,"百度地图启动...");

        HookByMatchName(TAG,
                Intent.class,
                null,"getBooleanExtra", String.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        if(TextUtils.equals((String)param.args[0], "start_up_splash_flag")){
                            param.setResult(false);
                        }
                    }
        });
    }
}
