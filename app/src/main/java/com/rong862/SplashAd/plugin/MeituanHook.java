package com.rong862.SplashAd.plugin;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;


public class MeituanHook extends BaseHook{

    private static final String TAG = "【美团】";

    public MeituanHook(){}

    @Override
    public void startHook() {

        log(TAG,"美团启动...");

        HookByMatchName(TAG,
                "com.meituan.android.pt.homepage.startup.StartupPicture$MaterialMap",
                null,"hasImageList",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        debug(TAG, "hasImageList-->" + param.getResult());
                        param.setResult(false);
                    }
        });

        HookByMatchName(TAG,
                "com.meituan.android.pt.homepage.startup.StartupPicture$MaterialMap",
                null,"hasVideoList",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        debug(TAG, "hasVideoList-->" + param.getResult());
                        param.setResult(false);
                    }
        });

        HookByMatchName(TAG,
                "com.meituan.android.pt.homepage.startup.StartupPicture$MaterialMap",
                null,"isAdPlatform",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        debug(TAG, "isAdPlatform-->" + param.getResult());
                        param.setResult(false);
                    }
        });
    }
}