package com.nimo.fb_effect.adapter;


import com.nimo.fb_effect.model.FBWatermarkConfig;

import java.util.List;

/**
 * @ClassName MtGreenScreenManager
 * @Description 绿幕配置文件管理
 * @Author yang
 * @Date 2021/6/23 10:26
 */
public class FBWaterMarkManager {

    private List<FBWatermarkConfig.FBWatermark> watermarks;


    public List<FBWatermarkConfig.FBWatermark> getWatermarks() {
        return watermarks;
    }


    public void setGreenscreens(List<FBWatermarkConfig.FBWatermark> watermarks) {
        this.watermarks = watermarks;
    }


    /**
     * 用于查找对应的绿幕配置
     *
     * @param name 名称
     * @return 对应的绿幕配置
     */
    public FBWatermarkConfig.FBWatermark findWatermark(String name) {
        // Iterator iterator = this.greenscreens.iterator();
        // MtGreenScreen mtGreenScreen;

        for (FBWatermarkConfig.FBWatermark screen : watermarks) {
            if (screen.getName().equals(name)) {
                return screen;
            }
        }
        return null;
        //
        // do {
        //     if (!iterator.hasNext()) {
        //         return null;
        //     }
        // } while (!name.equals((mtGreenScreen = (MtGreenScreen) iterator.next()).getName()));
        //
        // Log.e("find_screen", mtGreenScreen.getName());
        //
        // return mtGreenScreen;
    }

}
