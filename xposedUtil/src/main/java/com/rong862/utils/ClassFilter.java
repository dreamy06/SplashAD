package com.rong862.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.DexClass;

import static com.rong862.utils.LogUtil.debug;
import static com.rong862.utils.LogUtil.error;
import static com.rong862.utils.LogUtil.log;
import static com.rong862.utils.XposedUtil.CL;

public final class ClassFilter {

	private static final String TAG = "【ClassFilter】";

    private static final Map<String, Object> classesCache = new HashMap<>();

    /**
     * 获取apk文件的全部类
     * @param ApkPath apk文件路径
     * @return List<String> 类列表
     * */
    public static List<String> getClassNameList(String ApkPath){
    	
    	if(ApkPath == null) {
    		error(TAG, "ApkPath is null !!");
    		return null;
    	}
    	
        List<String> mClasses = new ArrayList<>();
        ApkFile apkFile = null;
        
        try {
            apkFile = new ApkFile(ApkPath);
            DexClass[] dexClasses = apkFile.getDexClasses();
            for (DexClass dexClass : dexClasses) {
            	mClasses.add(getClassName(dexClass));
            }
        }catch (Error | Exception e) {
        	error(TAG, "Open ApkFile error: " + e);
            return null;
        }finally{
            try {
                if(apkFile != null)
                    apkFile.close();
            } catch (Error | Exception e) {
            	error(TAG, "Close apkFile error: " + e);
            }
        }
        
        return mClasses;
    }

    /**
     * DexClass 转String类型的类名称
     * @param clazz DexClass
     * @return 类型的类名称
     * */
    private static String getClassName(DexClass clazz) {

        String str = clazz.getClassType().replace('/', '.');
        return str.substring(1, str.length() - 1);
    }

    /**
     * 获取类参数字符串
     * @param clazzes 类的参数数组
     * @return 类参数字符串
     * */
    public static String getParametersString(Class<?>... clazzes) {

        StringBuilder sb = new StringBuilder("(");

        boolean first = true;

        for (Class<?> clazz : clazzes) {
            if (first)
                first = false;
            else
                sb.append(",");

            if (clazz != null)
                sb.append(clazz.getCanonicalName());
            else
                sb.append("null");
        }
        sb.append(")");

        return sb.toString();
    }

    public static Class<?> findClassIfExists(String className, ClassLoader classLoader) {

        Class<?> c = null;

        try {
            c = Class.forName(className, false, classLoader);
        } catch (Error | Exception ignored) {

        }
        return c;
    }

    public static Field findFieldIfExists(Class<?> clazz, String fieldName) {

        if (clazz == null) {
            error(TAG, "findFieldIfExists error: clazz is null !");
            return null;
        }
        Field field = null;
        try {
            field = clazz.getField(fieldName);
        } catch (Error | Exception ignored) {
        }
        return field;
    }

    /**
     * 过滤到defaultPackage的类，根路径没有包名的类
     * @param loader 类加载器
     * @param classes 类列表
     * @return 过滤后的Classes对象
     * */
    public static Classes findDefaultClasses(ClassLoader loader, List<String> classes){
    	

        if (classesCache.containsKey("defaultPackage")) {
            return (Classes) classesCache.get("defaultPackage");
        }

        List<String> classNameList = new ArrayList<>();

        for (int i = 0; i < classes.size(); i++) {

            String clazz = classes.get(i);
            //过滤到没有包名的类
            if(!clazz.contains("."))classNameList.add(clazz);
        }

        List<Class<?>> classList = new ArrayList<>();

        for (int i = 0; i < classNameList.size(); i++) {

            String className = classNameList.get(i);

            Class<?> c = findClassIfExists(className, loader);

            if (c != null) {
                classList.add(c);
            }
        }
        
        debug(TAG, "get DefaultClasses num :" + classList.size());

        Classes cs = new Classes(classList);

        classesCache.put("defaultPackage", cs);

        return cs;
    }

    /**
     * 按包路径过滤类
     * @param loader 类加载器
     * @param classes 类列表
     * @param packageName 包名
     * @param depth 深度;   find class c ---> class :a.b.c  , package: a  , depth: 1,  get: a.*.*
     * @return 过滤后的Classes对象
     * */
    public static Classes findClassesFromPackage(ClassLoader loader, List<String> classes, String packageName, int depth) {

        if (classesCache.containsKey(packageName + depth)) {
            return (Classes) classesCache.get(packageName + depth);
        }

        List<String> classNameList = new ArrayList<>();

        for (int i = 0; i < classes.size(); i++) {

            String clazz = classes.get(i);

            //排除掉没有包名的类
            if(!clazz.contains("."))continue;

            String currentPackage = clazz.substring(0, clazz.lastIndexOf("."));

            for (int j = 0; j < depth; j++) {

                int pos = currentPackage.lastIndexOf(".");

                if (pos < 0)
                    break;

                currentPackage = currentPackage.substring(0, currentPackage.lastIndexOf("."));

            }

            if (currentPackage.equals(packageName)) {

                classNameList.add(clazz);
            }
        }

        List<Class<?>> classList = new ArrayList<>();

        for (int i = 0; i < classNameList.size(); i++) {

            String className = classNameList.get(i);

            Class<?> c = findClassIfExists(className, loader);

            if (c != null) {
                classList.add(c);
            }
        }
        
        debug(TAG, "get Classes num :" + classList.size());

        Classes cs = new Classes(classList);

        classesCache.put(packageName + depth, cs);

        return cs;
    }

    /**
     * 从类变量中搜索含有某个方法的第一个类
     * @param classOrName 目标类
     * @param returnType 方法返回类型
     * @param parameterTypes 方法参数数组
     * @return 符合筛选条件的类，如没有则返回null
     * */
    public static Class<?> findfirstClassFromField(Object classOrName, Class<?> returnType, Class<?>... parameterTypes){

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findfirstClassFromField: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findfirstClassFromField error: Class is null !");
            return null;
        }

        Class<?> fieldClass;

        Class<?> result = null;

        for (Field field : clazz.getDeclaredFields()) {

            fieldClass = field.getType();

            if(fieldClass.getName().startsWith("android") || fieldClass.getName().startsWith("java"))
                continue;

            if(findFirstMethodByMatchParameters(fieldClass, returnType, parameterTypes) != null){
                result = fieldClass;
                break;
            }

        }
        return result;
    }

    /**
     * 从类变量中搜索含有某个方法的类 list
     * @param classOrName 目标类
     * @param returnType 方法返回类型
     * @param parameterTypes 方法参数数组
     * @return 符合筛选条件的类 list，如没有则返回null
     * */
    public static List<Class<?>> findClassesFromField(Object classOrName, Class<?> returnType, Class<?>... parameterTypes){

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findClassesFromField: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findClassesFromField error: Class is null !");
            return null;
        }

        Class<?> fieldClass;

        List<Class<?>> result = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {

            fieldClass = field.getType();

            if(fieldClass.getName().startsWith("android") || fieldClass.getName().startsWith("java"))
                continue;

            if(findFirstMethodByMatchParameters(fieldClass, returnType, parameterTypes) != null){
                result.add(fieldClass);
            }

        }
        
        if (result.isEmpty()){
            return null;
        }
        
        return result;
    }

    /**
     * getMethods 模糊参数搜索类的public方法，包括父类的public方法
     * @param classOrName 目标类
     * @param returnType 方法返回类型，通配类型传：null
     * @param parameterTypes 方法参数数组，通配类型传：null
     * @return 符合筛选条件的方法 list，如没有则返回null
     * */
    public static List<Method> findPublicMethodsByMatchParameters(Object classOrName, Class<?> returnType, Class<?>... parameterTypes){

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findPublicMethodsByMatchParameters: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findPublicMethodsByMatchParameters error: Class is null !");
            return null;
        }

        List<Method> result = new LinkedList<>();

        try {
            for (Method method : clazz.getMethods()) {
                if (returnType != null && returnType != method.getReturnType())
                    continue;

                Class<?>[] methodParameterTypes = method.getParameterTypes();
                if (parameterTypes.length != methodParameterTypes.length)
                    continue;

                boolean match = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i] != null && parameterTypes[i] != methodParameterTypes[i]) {
                        match = false;
                        break;
                    }
                }

                if (!match)
                    continue;

                result.add(method);
            }
        }catch(Error | Exception e){
        	error(TAG, "findPublicMethodsByMatchParameters error :" + e);
        }

        if (result.isEmpty()){
            return null;
        }

        return result;
    }

    /**
     * 模糊参数搜索类的public方法，包括父类的public方法
     * @param classOrName 目标类
     * @param returnType 方法返回类型，通配类型传：null
     * @param parameterTypes 方法参数数组，通配类型传：null
     * @return 第一个符合筛选条件的方法，如没有则返回null
     * */
    public static Method findFirstPublicMethodByMatchParameters(Object classOrName, Class<?> returnType, Class<?>... parameterTypes){

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findFirstPublicMethodByMatchParameters: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findFirstPublicMethodByMatchParameters error: Class is null !");
            return null;
        }

        try {
            for (Method method : clazz.getMethods()) {
                if (returnType != null && returnType != method.getReturnType())
                    continue;

                Class<?>[] methodParameterTypes = method.getParameterTypes();
                if (parameterTypes.length != methodParameterTypes.length)
                    continue;

                boolean match = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i] != null && parameterTypes[i] != methodParameterTypes[i]) {
                        match = false;
                        break;
                    }
                }

                if (!match)
                    continue;

               return method;
            }
        }catch(Error | Exception e){
        	error(TAG, "findFirstPublicMethodsByMatchParameters error :" + e);
        }

        return null;
    }

    /**
     * 模糊参数搜索类的方法
     * @param classOrName 目标类
     * @param returnType 方法返回类型，通配类型传：null
     * @param parameterTypes 方法参数数组，通配类型传：null
     * @return 符合筛选条件的方法list，如没有则返回null
     * */
    public static List<Method> findMethodsByMatchParameters(Object classOrName, Class<?> returnType, Class<?>... parameterTypes) {

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findMethodsByMatchParameters: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findMethodsByMatchParameters error: Class is null !");
            return null;
        }

        List<Method> result = new LinkedList<>();

        try{
            for (Method method : clazz.getDeclaredMethods()) {

                if (returnType != null && returnType != method.getReturnType())
                    continue;

                Class<?>[] methodParameterTypes = method.getParameterTypes();

                if (parameterTypes.length != methodParameterTypes.length)
                    continue;

                boolean match = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    //参数类型null为通配
                    if (parameterTypes[i] != null && parameterTypes[i] != methodParameterTypes[i]) {
                        match = false;
                        break;
                    }
                }

                if (!match)
                    continue;

                method.setAccessible(true);
                result.add(method);
            }
        }catch(Error | Exception e){
        	error(TAG, "findMethodsByMatchParameters error :" + e);
        }

        if (result.isEmpty()) {
            return null;
        }

        return result;
    }

    /**
     * 模糊参数搜索类的方法
     * @param classOrName 目标类
     * @param returnType 方法返回类型，通配类型传：null
     * @param parameterTypes 方法参数数组，通配类型传：null
     * @return 第一个符合筛选条件的方法，如没有则返回null
     * */
    public static Method findFirstMethodByMatchParameters(Object classOrName, Class<?> returnType, Class<?>... parameterTypes) {

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findFirstMethodByMatchParameters: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findFirstMethodByMatchParameters error: Class is null !");
            return null;
        }

        try{
            for (Method method : clazz.getDeclaredMethods()) {

                if (returnType != null && returnType != method.getReturnType())
                    continue;

                Class<?>[] methodParameterTypes = method.getParameterTypes();

                if (parameterTypes.length != methodParameterTypes.length)
                    continue;

                boolean match = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    //参数类型null为通配
                    if (parameterTypes[i] != null && parameterTypes[i] != methodParameterTypes[i]) {
                        match = false;
                        break;
                    }
                }

                if (!match)
                    continue;

                method.setAccessible(true);
                return method;
            }
        }catch(Error | Exception e){
        	error(TAG, "findFirstMethodByMatchParameters error :" + e);
        }

        return null;
    }

    /**
     * 模糊参数搜索类的方法，优先本类的方法，如无则搜索父类public方法
     * @param clazz 目标类
     * @param returnType 方法返回类型，通配类型传：null
     * @param parameterTypes 方法参数数组，通配类型传：null
     * @return 第一个符合筛选条件的方法，如没有则返回null
     * */
    public static Method findFirstUnlimitedMethodByMatchParameters(Class<?> clazz, Class<?> returnType, Class<?>... parameterTypes) {

        if (clazz == null) {
            error(TAG, "findFirstUnlimitedMethodByMatchParameters error: Class is null !");
            return null;
        }

        Method method = findFirstMethodByMatchParameters(clazz, returnType, parameterTypes);

        if(method == null)
            return findFirstPublicMethodByMatchParameters(clazz, returnType, parameterTypes);
        else
            return method;
    }

    /**
     * 按方法名模糊搜索类的方法
     * @param classOrName 目标类
     * @param returnType 方法返回类型，通配类型传：null
     * @param parameterTypes 方法参数数组，通配类型传：null
     * @return 符合筛选条件的方法list，如没有则返回null
     * */
    public static List<Method> findMethodsByMatchName(Object classOrName, Class<?> returnType, String methodName, Class<?>... parameterTypes) {

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findMethodsByMatchName: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findMethodsByMatchName error: Class is null !");
            return null;
        }

        List<Method> result = new LinkedList<>();

        try{
            for (Method method : clazz.getDeclaredMethods()) {

                if (!methodName.equals(method.getName()))
                    continue;

                if(returnType != null && !returnType.getName().equals(method.getReturnType().getName()))
                    continue;

                Class<?>[] methodParameterTypes = method.getParameterTypes();

                if (parameterTypes.length != methodParameterTypes.length)
                    continue;

                boolean match = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    //参数类型null为通配
                    if (parameterTypes[i] != null && parameterTypes[i] != methodParameterTypes[i]) {
                        match = false;
                        break;
                    }
                }

                if (!match)
                    continue;

                method.setAccessible(true);
                result.add(method);
            }
        }catch(Error | Exception e){
        	error(TAG, "findMethodsByMatchName error :" + e);
        }

        if (result.isEmpty()) {
            return null;
        }

        return result;

    }

    /**
     * 按方法名模糊搜索类的方法
     * @param classOrName 目标类
     * @param returnType 方法返回类型，通配类型传：null
     * @param parameterTypes 方法参数数组，通配类型传：null
     * @return 第一个符合筛选条件的方法，如没有则返回null
     * */
    public static Method findFirstMethodByMatchName(Object classOrName, Class<?> returnType, String methodName, Class<?>... parameterTypes) {

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findFirstMethodByMatchName: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findFirstMethodByMatchName error: Class is null !");
            return null;
        }

        try{
            for (Method method : clazz.getDeclaredMethods()) {

                if (!methodName.equals(method.getName()))
                    continue;

                if(returnType != null && !returnType.getName().equals(method.getReturnType().getName()))
                    continue;

                Class<?>[] methodParameterTypes = method.getParameterTypes();

                if (parameterTypes.length != methodParameterTypes.length)
                    continue;

                boolean match = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    //参数类型null为通配
                    if (parameterTypes[i] != null && parameterTypes[i] != methodParameterTypes[i]) {
                        match = false;
                        break;
                    }
                }

                if (!match)
                    continue;

                method.setAccessible(true);
                return method;
            }
        }catch(Error | Exception e){
        	error(TAG, "findFirstMethodsByMatchName error :" + e);
        }

        return null;
    }


    /**
     * 按方法名模糊搜索类的Public方法,包含父类
     * @param classOrName 目标类
     * @param returnType 方法返回类型，通配类型传：null
     * @param parameterTypes 方法参数数组，通配类型传：null
     * @return 第一个符合筛选条件的方法，如没有则返回null
     * */
    public static Method findFirstPublicMethodByMatchName(Object classOrName, Class<?> returnType, String methodName, Class<?>... parameterTypes) {

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findFirstPublicMethodByMatchName: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findFirstPublicMethodByMatchName error: Class is null !");
            return null;
        }

        try{
            for (Method method : clazz.getMethods()) {

                if (!methodName.equals(method.getName()))
                    continue;

                if(returnType != null && !returnType.getName().equals(method.getReturnType().getName()))
                    continue;

                Class<?>[] methodParameterTypes = method.getParameterTypes();

                if (parameterTypes.length != methodParameterTypes.length)
                    continue;

                boolean match = true;
                for (int i = 0; i < parameterTypes.length; i++) {
                    //参数类型null为通配
                    if (parameterTypes[i] != null && parameterTypes[i] != methodParameterTypes[i]) {
                        match = false;
                        break;
                    }
                }

                if (!match)
                    continue;

                method.setAccessible(true);
                return method;
            }
        }catch(Error | Exception e){
            error(TAG, "findFirstMethodsByMatchName error :" + e);
        }

        return null;
    }

    /**
     * 按方法名模糊搜索方法，优先本类的方法，如无则搜索父类public方法
     * @param clazz 目标类
     * @param returnType 方法返回类型，通配类型传：null
     * @param parameterTypes 方法参数数组，通配类型传：null
     * @return 第一个符合筛选条件的方法，如没有则返回null
     * */
    public static Method findFirstUnlimitedMethodByMatchName(Class<?> clazz, Class<?> returnType, String methodName, Class<?>... parameterTypes) {

        if (clazz == null) {
            error(TAG, "findFirstUnlimitedMethodByMatchName error: Class is null !");
            return null;
        }

        Method method = findFirstMethodByMatchName(clazz, returnType, methodName, parameterTypes);

        if(method == null)
            return findFirstPublicMethodByMatchName(clazz, returnType, methodName, parameterTypes);
        else
            return method;
    }

    /**
     * 按变量名称搜索变量
     * @param classOrName 目标类
     * @param typeName 变量名称，SimpleName
     * @return 符合筛选条件的变量list，如没有则返回null
     * */
    public static List<Field> findFieldsWithType(Object classOrName, String typeName) {

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findFieldsWithType: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findFieldsWithType error: Class is null !");
            return null;
        }

        List<Field> list = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (fieldType.getSimpleName().equals(typeName)) {
                list.add(field);
            }
        }
        return list;
    }

    /**
     * 按变量名称搜索变量
     * @param classOrName 目标类或者类名全路径
     * @param typeName 变量名称，SimpleName
     * @return 第一个符合筛选条件的变量，如没有则返回null
     * */
    public static Field findFirstFieldWithType(Object classOrName, String typeName) {

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findFirstFieldWithType: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findFirstFieldWithType error: Class is null !");
            return null;
        }

        for (Field field : clazz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (fieldType.getSimpleName().equals(typeName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * 按变量名称搜索Public变量，包含父类Public变量
     * @param classOrName 目标类
     * @param typeName 变量名称，SimpleName
     * @return 第一个符合筛选条件的变量，如没有则返回null
     * */
    public static Field findFirstPublicFieldWithType(Object classOrName, String typeName) {

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findFirstPublicFieldWithType: classOrName must be class or String !");

        if (clazz == null) {
            error(TAG, "findFirstPublicFieldWithType error: Class is null !");
            return null;
        }

        for (Field field : clazz.getFields()) {
            Class<?> fieldType = field.getType();
            if (fieldType.getSimpleName().equals(typeName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * 按变量名称搜索变量，优先本类的变量，如无则搜索父类Public变量
     * @param clazz 目标类
     * @param typeName 变量名称，SimpleName
     * @return 第一个符合筛选条件的变量，如没有则返回null
     * */
    public static Field findFirstUnlimitedFieldWithType(Class<?> clazz, String typeName) {

        if (clazz == null) {
            error(TAG, "findFirstUnlimitedFieldWithType error: Class is null !");
            return null;
        }

        Field field = findFirstFieldWithType(clazz, typeName);

        if(field == null)
            return findFirstPublicFieldWithType(clazz, typeName);
        else
            return field;
    }

    /**
     * 按变量名称搜索Public变量，包含父类Public变量
     * @param classOrName 目标类
     * @param typeName 变量名称，SimpleName
     * @return 符合筛选条件的变量list，如没有则返回null
     * */
    public static List<Field> findPublicFieldsWithType(Object classOrName, String typeName) {

        Class<?> clazz = null;

        if(classOrName instanceof String)
            clazz = findClassIfExists((String)classOrName, CL);
        else if(classOrName instanceof Class)
            clazz = (Class<?>)classOrName;
        else
            error(TAG, "findPublicFieldsWithType: classOrName must be class or String !");

        List<Field> list = new ArrayList<>();
        if (clazz == null) {
            return list;
        }

        Field[] fields = clazz.getFields();

        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (fieldType.getSimpleName().equals(typeName)) {
                list.add(field);
            }
        }
        return list;
    }

    public static final class Classes {

        private final List<Class<?>> classes;

        public Classes(List<Class<?>> list) {
            this.classes = list;
        }

        /**
         * 筛选不存在某方法的类
         * @param returnType 方法返回类型
         * @param clsArr 方法参数数组
         * @return Classes
         * */
        public Classes filterByNoMethod(Class<?> returnType, Class<?>... clsArr) {

            List<Class<?>> arrayList = new ArrayList<>();

            for (Class<?> next : this.classes) {

                if (findFirstMethodByMatchParameters(next, returnType, Arrays.copyOf(clsArr, clsArr.length)) == null) {
                    arrayList.add(next);
                }
            }
            return new Classes(arrayList);
        }

        /**
         * 筛选存在某个方法的类
         * @param returnType 方法返回类型，参数null为通配
         * @param clsArr 方法参数数组，参数null为通配
         * @return Classes
         * */
        public Classes filterByMethodMatchParameters(Class<?> returnType, Class<?>... clsArr) {

            List<Class<?>> arrayList = new ArrayList<>();

            for (Class<?> next : this.classes) {
                if (findFirstMethodByMatchParameters(next, returnType, Arrays.copyOf(clsArr, clsArr.length)) != null) {
                    arrayList.add(next);
                }
            }

            return new Classes(arrayList);
        }

        /**
         * 筛选方法数量少于某个数量的类
         * @param count 类方法数量
         * @return Classes
         * */
        public Classes filterByMethodCountLess(int count){

            List<Class<?>> arrayList = new ArrayList<>();

            for (Class<?> next : this.classes) {
                if (next.getDeclaredMethods().length < count){
                    arrayList.add(next);
                }
            }
            return new Classes(arrayList);
        }

        /**
         * 筛选方法数量大于某个数量的类
         * @param count 类方法数量
         * @return Classes
         * */
        public Classes filterByMethodCountUp(int count){

            List<Class<?>> arrayList = new ArrayList<>();

            for (Class<?> next : this.classes) {
                if (next.getDeclaredMethods().length > count){
                    arrayList.add(next);
                }
            }
            return new Classes(arrayList);
        }


        /**
         * 筛选存在某个方法的类
         * @param methodName 方法名称
         * @param parameterTypes 方法参数数组，参数null为通配
         * @return Classes
         * */
        public Classes filterByMethodMatchName(String methodName, Class<?>... parameterTypes) {

            List<Class<?>> arrayList = new ArrayList<>();
            for (Class<?> next : this.classes) {
                if(findFirstMethodByMatchName(next, null, methodName, Arrays.copyOf(parameterTypes, parameterTypes.length)) != null){
                    arrayList.add(next);
                }
            }
            return new Classes(arrayList);
        }

        /**
         * 筛选存在某个方法的类
         * @param returnType 方法返回类型
         * @param methodName 方法名称
         * @param parameterTypes 方法参数数组，参数null为通配
         * @return Classes
         * */
        public Classes filterByMethodMatchName(Class<?> returnType, String methodName, Class<?>... parameterTypes) {

            List<Class<?>> arrayList = new ArrayList<>();
            for (Class<?> next : this.classes) {
                if(findFirstMethodByMatchName(next, returnType, methodName, Arrays.copyOf(parameterTypes, parameterTypes.length)) != null){
                    arrayList.add(next);
                }
            }
            return new Classes(arrayList);
        }

        /**
         * 筛选不存在某变量的类
         * @param fieldType 变量类型名称，为SimpleName
         * @return Classes
         * */
        public Classes filterByNoField(String fieldType) {

            List<Class<?>> arrayList = new ArrayList<>();
            for (Class<?> next : this.classes) {
                if (findFirstFieldWithType(next, fieldType) == null) {
                    arrayList.add(next);
                }
            }

            return new Classes(arrayList);
        }

        /**
         * 筛选存在某变量的类
         * @param fieldType 变量类型名称，为SimpleName
         * @return Classes
         * */
        public Classes filterByField(String fieldType) {

            List<Class<?>> arrayList = new ArrayList<>();
            for (Class<?> next : this.classes) {
                if (findFirstFieldWithType(next, fieldType) != null) {
                    arrayList.add(next);
                }
            }

            return new Classes(arrayList);
        }

        /**
         * 筛选存在某变量的类
         * @param fieldName 变量名称
         * @param fieldType 变量类型名称，为SimpleName
         * @return Classes
         * */
        public Classes filterByField(String fieldName, String fieldType) {

            List<Class<?>> arrayList = new ArrayList<>();

            for (Class<?> next : this.classes) {

                Field field = findFieldIfExists(next, fieldName);

                if (field != null && field.getType().getSimpleName().equals(fieldType)) {
                    arrayList.add(next);
                }
            }

            return new Classes(arrayList);
        }

        /**
         * 符合条件的第一个类，如没有则返回null
         * @return Class
         * */
        public Class<?> firstOrNull() {

            if (this.classes.isEmpty())
                return null;
            
            if(this.classes.size() > 1) {
            	
            	log(TAG, "fine too many class:");
            	
            	for(int i = 0; i < this.classes.size(); i++) {
            		log(TAG, i + "--class name: " + this.classes.get(i).getName());
            	}
            }

            return this.classes.get(0);
        }
    }
}

