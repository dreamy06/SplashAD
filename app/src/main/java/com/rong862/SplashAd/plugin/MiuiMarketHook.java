package com.rong862.SplashAd.plugin;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;

public class MiuiMarketHook extends BaseHook{

    private static final String TAG = "【小米市场】";

    public MiuiMarketHook(){}

    @Override
    public void startHook() {

        log(TAG,"小米市场启动...");

        HookByMatchName(TAG,
                "com.xiaomi.market.ui.splash.SplashManager",
                null,"tryAdSplash", android.app.Activity.class, String.class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam){

                        debug(TAG,"tryAdSplash set null !");
                        return null;
                    }
        });
    }
}
