package com.rong862.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.XModuleResources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.rong862.utils.ClassFilter.findClassIfExists;
import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.error;
import static com.rong862.utils.LogUtil.log;

public abstract class XposedUtil implements IXposedHookLoadPackage, IXposedHookZygoteInit{

    private static final String TAG = "【XposedUtil】";
    /**app context*/
    public static Context appContext;
    /**app ClassLoader*/
    public static ClassLoader CL;
    /**apk file path*/
    public static String apkPath;
    /**apk file path*/
    public static String loadPackageName;
    /**app 版本号*/
    public static String VersionCode;
    /**插件res资源*/
    public static XModuleResources Xres;
    /**SharedPreferences*/
    public static ConfigUtil SP;

    private static boolean isLoadPlugin = false;
    private static boolean isHookApp = false;
    /**app packageName*/
    private static String packageName = "all";
    /**app SharedPreferences name*/
    private static String SharedPreferencesName;
    /**plugin app provider Name*/
    private static String providerName;
    /**SharedPreferences 配置类型， 1：宿主配置  2：模块配置*/
    private static int SharedPreferencesMode = 0;
    /**hook class list*/
    private static final List<String> hookClassList = new ArrayList<>();

    //加载hook代码
    public abstract void loadPlugin();

    public void loadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam){}

    /**
     * 设置配置文件名称, 配置生成在宿主app里使用
     * @param preName 配置名称
     * */
    public static void setSharedPreferences(String preName){
        SharedPreferencesName = preName;
        SharedPreferencesMode = 1;
    }

    /**
     * 设置配置文件, 读取模块app的配置时使用
     * @param preName 配置名称
     * @param providername 模块app的provider name
     * */
    public static void setSharedPreferences(String preName, String providername){
        SharedPreferencesName = preName;
        SharedPreferencesMode = 2;
        providerName = providername;
    }

    /**
     * 设置配置文件名称, 配置生成在宿主app里使用
     * @param preName 配置名称
     * */
    public static void initConfig(String preName){

        if(SharedPreferencesMode != 0){
            log(TAG, "Config is already init , name: " + SharedPreferencesName);
            return;
        }
        SP = new ConfigUtil(appContext, preName);
    }

    /**
     * 设置配置文件名称, 读取模块app的配置时使用
     * @param preName 配置名称
     * @param providername 模块app的provider name
     * */
    public static void initConfig(String preName, String providername){

        if(SharedPreferencesMode != 0){
            log(TAG, "Config is already init , name: " + SharedPreferencesName);
            return;
        }
        SP = new ConfigUtil(appContext, preName, providername);
    }

    public static void setPackageName(String pName){packageName = pName;}
    public static void setLogTag(String logTag){LogUtil.setLogTag(logTag);}
    public static void setDebug(boolean isDebug){LogUtil.setDebug(isDebug);}

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam){

        debug(TAG, "modulePath : " + startupParam.modulePath);
        //获取插件的res资源
        Xres = XModuleResources.createInstance(startupParam.modulePath, null);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam){

        loadPackage(loadPackageParam);

        if(!packageName.equals("all") && !packageName.equals(loadPackageParam.packageName))
                return;

        if(isHookApp)
            return;

        isHookApp = true;

        debug(TAG,"load package success: " + loadPackageParam.packageName);

        apkPath = loadPackageParam.appInfo.sourceDir;
        loadPackageName = loadPackageParam.packageName;

        //360加固类
        Class<?> qihooClass = findClassIfExists("com.stub.StubApp", loadPackageParam.classLoader);
        //腾讯加固类
        Class<?> TxShellClass = findClassIfExists("com.tencent.StubShell.TxAppEntry", loadPackageParam.classLoader);

        if(qihooClass != null) {

            log(TAG,"****360加固了**** :" + loadPackageParam.packageName);

            XposedHelpers.findAndHookMethod(qihooClass, "a", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    init((Context)param.args[0]);
                }
            });
        }else if(TxShellClass != null){

            log(TAG,"****腾讯加固了**** :" + loadPackageParam.packageName);

            XposedHelpers.findAndHookMethod(TxShellClass, "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    init((Context)param.args[0]);
                }
            });
        }else{
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    init((Context)param.args[0]);
                }
            });
        }
    }

    private void init(Context context){

        //防止重复加载插件
        if(isLoadPlugin)
            return;
        isLoadPlugin = true;

        appContext = context;
        CL = appContext.getClassLoader();
        VersionCode = VersionUtil.getVersionCode(appContext);

        if(SharedPreferencesMode == 1)
            SP = new ConfigUtil(appContext, SharedPreferencesName);
        else if(SharedPreferencesMode == 2)
            SP = new ConfigUtil(appContext, SharedPreferencesName, providerName);

        loadPlugin();
    }
    //判断类是否已经执行过hook代码，避免重复hook
    public static boolean isHook(Class<?> clz){

        if(hookClassList.contains(clz.getName()))return true;

        hookClassList.add(clz.getName());

        return false;
    }

    /**
     * 获取模块的布局
     * @param mContext 当前视图上下文
     * @param id 资源id
     * @return : View
     * */
    public static View getViewById(Context mContext, int id){

        return LayoutInflater.from(mContext).inflate(Xres.getLayout(id),null);
    }

    /**
     * 获取模块的Drawable
     * @param id 资源id
     * @return : Drawable
     * */
    public static Drawable getDrawableById(int id){

        return Xres.getDrawable(id, null);
    }

    /**
     * 获取模块的布局
     * @param idName 资源id名称
     * @return : 资源ID
     * */
    public static int getIdByName(String idName){

        return appContext.getResources().getIdentifier(idName, "id", appContext.getPackageName());
    }
}
