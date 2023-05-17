package com.rong862.SplashAd.plugin;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;

public class MiPayHook extends BaseHook{

    private static final String TAG = "【小米钱包】";

    public MiPayHook(){}

    @Override
    public void startHook() {

        log(TAG,"小米钱包启动...");

        HookByMatchName(TAG,
                "com.xiaomi.jr.app.MiFinanceActivity",
                null,"onCreate", android.os.Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        for(Field field : param.thisObject.getClass().getDeclaredFields()){
                            if(field.getType().getName().equals("com.xiaomi.jr.app.splash.SplashFragment")){
                                field.setAccessible(true);
                                Object splashFragment = field.get(param.thisObject);
                                XposedHelpers.callMethod(splashFragment, "close");
                                break;
                            }
                        }
                    }
        });
    }
}
