package com.rong862.SplashAd.plugin;

import android.app.Activity;
import android.content.Intent;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static com.rong862.utils.LogUtil.error;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;
import static com.rong862.utils.XposedUtil.CL;


public class KugouHook extends BaseHook{

    private static final String TAG = "【酷狗】";

    public KugouHook(){}

    @Override
    public void startHook() {

        log(TAG,"酷狗启动...");

        Class<?> mainClass = XposedHelpers.findClassIfExists("com.kugou.android.app.MediaActivity", CL);

        if(mainClass == null){
            error(TAG,"MediaActivity Class is not exit !");
            return;
        }

        HookByMatchName(TAG,
                "com.kugou.android.app.splash.SplashActivity",
                null,"onCreate", android.os.Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        Activity AdActivity = (Activity)param.thisObject;
                        Intent intent = new Intent(AdActivity, mainClass);
                        AdActivity.startActivity(intent);
                        AdActivity.finish();
                    }
        });
    }
}
