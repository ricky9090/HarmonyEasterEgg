package com.example.harmonyeasteregg.hook;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class MyUtils {

    public static final String PLAT_LOGO_TAG = "plat_logo_activity";

    public static Activity getCurrentActivity () {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(
                    null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void hook() {
        try {
            Object singleTon = null;

            Class<?> activityManagerClass = Class.forName("android.app.ActivityTaskManager");
            Field iActivityManagerSingletonField = activityManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
            iActivityManagerSingletonField.setAccessible(true);
            singleTon = iActivityManagerSingletonField.get(null);

            Class<?> singleTonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singleTonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);

            // Android 10版本 IActivityTaskManagerSingleton 对象
            final Object mInstance = mInstanceField.get(singleTon);

            Class<?> iActivityManagerClass = Class.forName("android.app.IActivityTaskManager");

            Object newInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{iActivityManagerClass},
                    new InvocationHandler() {

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                            if (method.getName().equals("startActivity")) {

                                int index = 0;

                                for (int i = 0; i < args.length; i++) {
                                    if (args[i] instanceof Intent) {
                                        index = i;
                                        break;
                                    }
                                }
                                Intent orgIntent = (Intent) args[index];
                                if (!orgIntent.getBooleanExtra(MyUtils.PLAT_LOGO_TAG, false)) {
                                    return method.invoke(mInstance, args);
                                }
                                Intent proxyIntent = new Intent();
                                proxyIntent.setClassName("com.example.harmonyeasteregg",
                                        "com.example.harmonyeasteregg.MainAbilityShellActivity");
                                proxyIntent.putExtra(MyUtils.PLAT_LOGO_TAG, true);
                                args[index] = proxyIntent;
                            }
                            return method.invoke(mInstance, args);
                        }
                    });

            mInstanceField.set(singleTon, newInstance);

            hookHandler();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hookHandler() throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {

        Class<?> ActivityThreadclass = Class.forName("android.app.ActivityThread");

        Field sCurrentActivityThreadFiled = ActivityThreadclass.getDeclaredField(
                "sCurrentActivityThread");
        sCurrentActivityThreadFiled.setAccessible(true);
        Object sCurrentActivityThread = sCurrentActivityThreadFiled.get(null);

        Field mHFiled = ActivityThreadclass.getDeclaredField("mH");
        mHFiled.setAccessible(true);
        Object mH = mHFiled.get(sCurrentActivityThread);

        Field mCallbackFiled = Handler.class.getDeclaredField("mCallback");
        mCallbackFiled.setAccessible(true);

        mCallbackFiled.set(mH, new ActivityThreadHandlerCallback((Handler) mH));
    }
}
