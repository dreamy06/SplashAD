package com.rong862.SplashAd.plugin;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;

public class ZhixingHook extends BaseHook{

    private static final String TAG = "【智行出行】";

    public ZhixingHook(){}

    @Override
    public void startHook() {

        log(TAG,"智行出行启动...");

        HookByMatchName(TAG,
                "com.app.base.tripad.TripAdManager",
                null,"getCanShowSplashAdMark",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        debug(TAG,"getCanShowSplashAdMark getResult:" + param.getResult());
                        param.setResult(false);
                    }
        });
    }
}