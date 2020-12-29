package com.example.harmonyeasteregg.hook;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.util.List;

public class ActivityThreadHandlerCallback implements Handler.Callback {

    Handler mBase;

    public ActivityThreadHandlerCallback(Handler base) {
        mBase = base;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 159) {
            Object obj = msg.obj;
            try {
                Class<?> clazz = Class.forName("android.app.servertransaction.ClientTransaction");
                Field mActivityCallbacksFiled = clazz.getDeclaredField("mActivityCallbacks");
                mActivityCallbacksFiled.setAccessible(true);
                List list = (List) mActivityCallbacksFiled.get(obj);
                if (list != null && list.size() > 0) {
                    for (Object o : list) {
                        try {
                            Class<?> LaunchActivityItemClazz = Class.forName("android.app" +
                                    ".servertransaction.LaunchActivityItem");
                            Field mIntentFiled = LaunchActivityItemClazz.getDeclaredField("mIntent");
                            mIntentFiled.setAccessible(true);
                            Intent intent = (Intent) mIntentFiled.get(o);
                            if (intent.getBooleanExtra(MyUtils.PLAT_LOGO_TAG, false)) {
                                android.content.Intent easterEggIntent = new android.content.Intent();
                                easterEggIntent.setClassName(
                                        "android",
                                        "com.android.internal.app.PlatLogoActivity");
                                mIntentFiled.set(o, easterEggIntent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mBase.handleMessage(msg);
        return true;
    }
}
