package com.rong862.SplashAd.plugin;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;

public class LeKeHook extends BaseHook {

    private static final String TAG = "【乐刻运动】";

    public LeKeHook() {}

    @Override
    public void startHook() {

        log(TAG,"乐刻运动启动...");

        HookByMatchName(TAG,
                "com.leoao.fitness.view.AdverisementView",
                null, "checkAdvertisement",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.setResult(null);
                    }
        });

        HookByMatchName(TAG,
                "com.leoao.fitness.main.MainActivity",
                null, "handleMainDialogs",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.setResult(null);
                    }
        });
    }
}
