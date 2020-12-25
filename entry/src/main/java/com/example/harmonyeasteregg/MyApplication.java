package com.example.harmonyeasteregg;

import com.example.harmonyeasteregg.hook.MyUtils;
import ohos.aafwk.ability.AbilityPackage;

public class MyApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        super.onInitialize();
        MyUtils.hook();
    }
}
