package com.nimo.facebeauty;

import android.app.Application;
import android.util.Log;

import com.nimo.facebeauty.tools.ToastUtils;
import com.nimo.facebeauty.FBEffect.InitCallback;
import com.nimo.fb_effect.FBPanelLayout;


public class FBApplication extends Application {

    // 鉴权是否完成
    public static boolean hasInit = false;

    @Override
    public void onCreate() {
        super.onCreate();

        //todo ---Face Beauty start----
        FBEffect.shareInstance().initFaceBeauty( this, "fbe396da4e7944a399eefe285db79cbd", new InitCallback() {
            @Override public void onInitSuccess() {
                hasInit = true;
//                new FBPanelLayout(FBApplication.this).init(null);
            }

            @Override public void onInitFailure() {
                hasInit = false;

            }
        });

        ToastUtils.init(this);
    }

}
