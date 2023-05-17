package com.rong862.SplashAd.plugin;

import com.rong862.SplashAd.Hook;
import com.rong862.utils.ClassFilter;
import com.rong862.utils.XposedUtil;

import java.util.List;
import java.util.Objects;

import de.robv.android.xposed.XC_MethodHook;

import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.error;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedPlus.HookByMatchName;
import static com.rong862.utils.XposedUtil.CL;
import static com.rong862.utils.XposedUtil.SP;
import static com.rong862.utils.XposedUtil.VersionCode;

public class ZhihuHook extends BaseHook{

    private static final String TAG = "【知乎】";
    private static final String CurVersion = "CurVesion";
    private static final String AdClassName = "AdClassName";

    public ZhihuHook(){
        XposedUtil.initConfig("SplashAD");
    }

    @Override
    public void startHook() {

        log(TAG,"知乎启动...");

        if(!Objects.equals(SP.getString(CurVersion, "0"), VersionCode)){
            if(!classSearch())
                return;
        }else if(Objects.equals(SP.getString(CurVersion, "0"), "fail")){
            error(TAG,"class search fail");
            return;
        }

        HookByMatchName(TAG,
                SP.getString(AdClassName),
                null,"isShowLaunchAd",
                new Hook() {
                    @Override
                    protected void after(MethodHookParam param){
                        debug(TAG,"isShowLaunchAd is --->" + param.getResult());
                        param.setResult(false);
                    }
        });
    }

    private boolean classSearch(){

        List<String> zhClasses = ClassFilter.getClassNameList(XposedUtil.apkPath);

        if(zhClasses == null)
            return false;

        log(TAG,"读取到类数量：" + zhClasses.size());

        Class<?> AdClass = ClassFilter.findClassesFromPackage(CL, zhClasses, "com.zhihu.android.app.util", 0)
                .filterByMethodMatchName(boolean.class, "isShowLaunchAd")
                .filterByMethodMatchName(void.class, "cleanLaunchAdStatus")
                .firstOrNull();

        if(AdClass == null){
            error(TAG, "AdClass is null !");
            SP.setString(CurVersion, "fail");
            return false;
        }

        log(TAG, "AdClass :" + AdClass.getName());

        SP.setString(AdClassName, AdClass.getName());
        SP.setString(CurVersion, VersionCode);

        return true;
    }
}
