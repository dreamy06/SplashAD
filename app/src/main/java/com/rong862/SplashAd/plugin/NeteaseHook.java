package com.rong862.SplashAd.plugin;

import android.app.Activity;
import android.content.Intent;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.ClassFilter.findClassIfExists;
import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;
import static com.rong862.utils.XposedUtil.CL;

public class NeteaseHook extends BaseHook{

    private static final String TAG = "【网易新闻】";

    public NeteaseHook(){}

    @Override
    public void startHook() {

        log(TAG,"网易新闻启动...");


        HookByMatchName(TAG,
                "com.netease.nr.biz.ad.newAd.AdActivity",
                null,"onCreate", android.os.Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        Activity AdActivity = (Activity)param.thisObject;
                        Intent intent = new Intent(AdActivity, findClassIfExists("com.netease.nr.phone.main.MainActivity", CL));
                        AdActivity.startActivity(intent);
                        debug(TAG,"NeteaseHook: AdActivity finish...");
                        AdActivity.finish();
                    }
        });

        HookByMatchName(TAG,
                "com.netease.newsreader.common.ad.e.a",
                null,"a", findClassIfExists("com.netease.newad.adinfo.AdInfo", CL),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        debug(TAG,"AdLoad set null...");
                        param.args[0] = null;
                    }
        });
    }
}
