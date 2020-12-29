package com.example.harmonyeasteregg.slice;

import com.example.harmonyeasteregg.ResourceTable;
import com.example.harmonyeasteregg.hook.MyUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Text;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        Text helloworld = (Text) findComponentById(ResourceTable.Id_text_helloworld);
        helloworld.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                openEasterEgg();
            }
        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    private void openEasterEgg() {
        try {
            android.content.Intent  intent = new android.content.Intent(android.content.Intent.ACTION_MAIN);
            intent.setClassName("android", "com.android.internal.app.PlatLogoActivity");
            intent.putExtra(MyUtils.PLAT_LOGO_TAG, true);
            MyUtils.getCurrentActivity().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
