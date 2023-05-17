package com.rong862.SplashAd.plugin;

import de.robv.android.xposed.XC_MethodReplacement;

import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookAllMethods;


public class MaimaiHook extends BaseHook{

    private static final String TAG = "【脉脉】";

    public MaimaiHook(){}

    @Override
    public void startHook() {

        log(TAG,"脉脉启动...");

        HookAllMethods(TAG,
                "com.taou.maimai.MainViewModel", "showColdStartAd",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam){

                        debug(TAG,"showColdStartAd is Replacemented...");
                        return null;
                    }
        });
    }
}
