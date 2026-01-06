package com.nimo.common;

import android.annotation.SuppressLint;
import android.app.Application;

public class FBApplication {

    // 使用 volatile 保证多线程环境下可见性和禁止指令重排
    private static volatile Application mApplication;

    // 私有构造，防止外部实例化
    private FBApplication() {}

    /**
     * 获取 Application 实例，线程安全，双重检查锁定
     */
    public static Application getApplication() {
        if (mApplication == null) {
            synchronized (FBApplication.class) {
                if (mApplication == null) {
                    mApplication = reflectionGetApplication();
                }
            }
        }
        return mApplication;
    }

    /**
     * 反射获取 Application 实例
     */
    @SuppressLint("PrivateApi")
    private static Application reflectionGetApplication() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            // 调用静态方法 currentApplication()
            Object application = activityThreadClass.getMethod("currentApplication").invoke(null);
            if (application instanceof Application) {
                return (Application) application;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
