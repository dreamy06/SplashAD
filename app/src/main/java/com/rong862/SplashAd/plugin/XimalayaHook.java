package com.rong862.SplashAd.plugin;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;


public class XimalayaHook extends BaseHook{

    private static final String TAG = "【喜马拉雅】";

    public XimalayaHook(){}

    @Override
    public void startHook() {

        log(TAG,"喜马拉雅启动...");

        HookByMatchName(TAG,
                "com.ximalaya.ting.android.adsdk.AdSDK",
                null, "getProvider",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.setResult(null);
                    }
        });
    }
}
