package com.nimo.facebeauty;

import android.app.Application;
import android.util.Log;

import com.nimo.facebeauty.tools.ToastUtils;
import com.nimo.facebeauty.FBEffect.InitCallback;


public class FBApplication extends Application {

    // 鉴权是否完成
    public static boolean hasInit = false;

    @Override
    public void onCreate() {
        super.onCreate();

        //todo ---Face Beauty start----
        FBEffect.shareInstance().initFaceBeauty( this, "YOUR_APP_ID", new InitCallback() {
            @Override public void onInitSuccess() {
                hasInit = true;

            }

            @Override public void onInitFailure() {
                hasInit = false;

            }
        });

        ToastUtils.init(this);
    }

}
