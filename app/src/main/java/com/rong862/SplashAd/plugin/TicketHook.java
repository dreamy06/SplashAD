package com.rong862.SplashAd.plugin;

import android.view.View;
import android.widget.ImageView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;

public class TicketHook extends BaseHook{

    private static final String TAG = "【12306】";

    public TicketHook(){}

    @Override
    public void startHook() {

        log(TAG,"12306启动...");

        HookByMatchName(TAG,
                "com.MobileTicket.ui.activity.MainActivity",
                null, "findViews",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        ImageView img = (ImageView)XposedHelpers.getObjectField(param.thisObject, "mSplashPlaceHolder");
                        img.setVisibility(View.GONE);
                    }
        });


        HookByMatchName(TAG,
                "com.MobileTicket.ui.dialog.SplashAdDialog",
                null,"show",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.setResult(null);
                    }
            });
    }
}
