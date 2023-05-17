package com.rong862.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.rong862.utils.ClassFilter.findClassIfExists;
import static com.rong862.utils.LogUtil.error;
import static com.rong862.utils.XposedUtil.CL;

public class XposedPlus {

    /**
     * HookByMatchName
     * @param pluginTag log标签
     * @param className 类全路径名称
     * @param returnType 方法返回类型，通配传null
     * @param methodName 方法名称
     * @param parameterTypesAndCallback 方法参数，通配传null，最后为hook回调类
     * */
    public static void HookByMatchName(String pluginTag, String className, Class<?> returnType, String methodName, Object... parameterTypesAndCallback){

        Class<?> clz = XposedHelpers.findClassIfExists(className, CL);

        if(clz != null){

            Method method = ClassFilter.findFirstUnlimitedMethodByMatchName(clz, returnType, methodName, getParameterClasses(parameterTypesAndCallback));

            if(method == null){
                error(pluginTag,"HookByMatchName error--> Method is not found: " + methodName);
                return;
            }

            XC_MethodHook callback = (XC_MethodHook) parameterTypesAndCallback[parameterTypesAndCallback.length-1];

            XposedBridge.hookMethod(method, callback);

        }else
            error(pluginTag,"HookByMatchName error--> Class is not found: " + className);
    }

    /**
     * HookByMatchName
     * @param pluginTag log标签
     * @param clz 类
     * @param returnType 方法返回类型，通配传null
     * @param methodName 方法名称
     * @param parameterTypesAndCallback 方法参数，通配传null，最后为hook回调类
     * */
    public static void HookByMatchName(String pluginTag, Class<?> clz, Class<?> returnType, String methodName, Object... parameterTypesAndCallback){

        if(clz != null){

            Method method = ClassFilter.findFirstUnlimitedMethodByMatchName(clz, returnType, methodName, getParameterClasses(parameterTypesAndCallback));

            if(method == null){
                error(pluginTag,"HookByMatchName error--> Method is not found: " + methodName);
                return;
            }

            XC_MethodHook callback = (XC_MethodHook) parameterTypesAndCallback[parameterTypesAndCallback.length-1];

            XposedBridge.hookMethod(method, callback);

        }else
            error(pluginTag,"HookByMatchName error--> Class is null , methodName: " + methodName);
    }

    /**
     * HookByMatchParameters
     * @param pluginTag log标签
     * @param methodName 方法名称
     * @param className 类全路径名称
     * @param returnType 方法返回类型，通配传null
     * @param parameterTypesAndCallback 方法参数，通配传null，最后为hook回调类
     * */
    public static void HookByMatchParameters(String pluginTag, String methodName, String className, Class<?> returnType, Object... parameterTypesAndCallback){

        Class<?> clz = XposedHelpers.findClassIfExists(className, CL);

        if(clz != null){

            Method method = ClassFilter.findFirstUnlimitedMethodByMatchParameters(clz, returnType, getParameterClasses(parameterTypesAndCallback));

            if(method == null){
                error(pluginTag,"HookByMatchParameters error--> Method is not found: " + methodName);
                return;
            }

            XC_MethodHook callback = (XC_MethodHook) parameterTypesAndCallback[parameterTypesAndCallback.length-1];

            XposedBridge.hookMethod(method, callback);

        }else
            error(pluginTag,"HookByMatchParameters error--> Class is not found: " + className);
    }

    /**
     * HookByMatchParameters
     * @param pluginTag log标签
     * @param methodName 需要搜索的方法名，log方便排查报错
     * @param clz 类
     * @param returnType 方法返回类型，通配传null
     * @param parameterTypesAndCallback 方法参数，通配传null，最后为XC_MethodHook回调类
     * */
    public static void HookByMatchParameters(String pluginTag, String methodName, Class<?> clz, Class<?> returnType, Object... parameterTypesAndCallback){

        if(clz != null){

            Method method = ClassFilter.findFirstUnlimitedMethodByMatchParameters(clz, returnType, getParameterClasses(parameterTypesAndCallback));

            if(method == null){
                error(pluginTag,"HookByMatchParameters error--> Method is not found: " + methodName);
                return;
            }

            XC_MethodHook callback = (XC_MethodHook) parameterTypesAndCallback[parameterTypesAndCallback.length-1];

            XposedBridge.hookMethod(method, callback);

        }else
            error(pluginTag,"HookByMatchParameters error--> Class is null,methodName: " + methodName);
    }

    public static void HookAllConstructors(String TAG, String className, XC_MethodHook Callback){

        Class<?> clz = XposedHelpers.findClassIfExists(className, CL);

        if(clz != null){
            XposedBridge.hookAllConstructors(clz, Callback);
        }else
            error(TAG,"HookConstructor error--> Class is not found: " + className);
    }

    public static void HookAllConstructors(String TAG, Class<?> clz, XC_MethodHook Callback){

        if(clz != null){
            XposedBridge.hookAllConstructors(clz, Callback);
        }else
            error(TAG,"HookConstructor error--> Class is not found");
    }

    public static void HookConstructor(String TAG, String className, Object... parameterTypesAndCallback){

        Class<?> clz = XposedHelpers.findClassIfExists(className, CL);

        if(clz != null){
            XposedHelpers.findAndHookConstructor(clz, parameterTypesAndCallback);
        }else
            error(TAG,"HookConstructor error--> Class is not found: " + className);
    }

    public static void HookConstructor(String TAG, Class<?> clz, Object... parameterTypesAndCallback){

        if(clz != null){
            XposedHelpers.findAndHookConstructor(clz, parameterTypesAndCallback);
        }else
            error(TAG,"HookConstructor error--> Class is not found");
    }


    public static void HookAllMethods(String TAG, String className, String methodName, XC_MethodHook Callback){

        Class<?> clz = XposedHelpers.findClassIfExists(className, CL);

        if(clz != null){

            boolean isHook = false;

            for(Method method : clz.getDeclaredMethods()){
                if(method.getName().equals(methodName)){
                    XposedBridge.hookMethod(method, Callback);
                    isHook = true;
                }
            }

            if(!isHook)
                error(TAG, "HookAllMethods error--> Method is not found: " + methodName);
        }else
            error(TAG,"HookAllMethods error--> Class is not found: " + className);
    }

    public static void HookAllMethods(String TAG, Class<?> clz, String methodName, XC_MethodHook Callback){

        if(clz != null){

            boolean isHook = false;

            for(Method method : clz.getDeclaredMethods()){
                if(method.getName().equals(methodName)){
                    XposedBridge.hookMethod(method, Callback);
                    isHook = true;
                }
            }

            if(!isHook)
                error(TAG, "HookAllMethods error--> Method is not found: " + methodName);
        }else
            error(TAG,"HookAllMethods error--> Class is not found, method name: " + methodName);
    }

    private static Class<?>[] getParameterClasses(Object[] parameterTypesAndCallback) {

        Class<?>[] parameterClasses = null;

        for (int i = parameterTypesAndCallback.length - 1; i >= 0; i--) {

            Object type = parameterTypesAndCallback[i];

            // ignore trailing callback
            if (type instanceof XC_MethodHook)
                continue;

            if (parameterClasses == null)
                parameterClasses = new Class<?>[i+1];

            if (type == null)
                parameterClasses[i] = null;
            else if (type instanceof Class)
                parameterClasses[i] = (Class<?>) type;
            else if(type instanceof String)
                parameterClasses[i] = findClassIfExists((String)type, CL);
            else
                parameterClasses[i] = null;
        }

        if (parameterClasses == null)
            parameterClasses = new Class<?>[0];

        return parameterClasses;
    }
}
