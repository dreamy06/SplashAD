package com.rong862.SplashAd;

import de.robv.android.xposed.XC_MethodHook;

public abstract class Hook extends XC_MethodHook {

    public Hook(){
        super();
    }

    public Hook(int priority){
        super(priority);
    }

    protected void before(MethodHookParam param) throws Throwable{}

    protected void after(MethodHookParam param) throws Throwable{}

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        before(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        after(param);
    }
}
