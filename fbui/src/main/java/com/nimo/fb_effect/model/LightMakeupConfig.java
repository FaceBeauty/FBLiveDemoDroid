package com.nimo.fb_effect.model;
import android.util.Log;

import com.nimo.facebeauty.FBEffect;

import java.io.File;
import java.util.List;

/**
 * 妆容推荐配置
 */

public class LightMakeupConfig {

    /**
     * makeups
     */
    private List<LightMakeup> light_makeup;

    @Override public String toString() {
        return "LightMakeupConfig{" +
                "htStyles=" + light_makeup.size() +
                "个}";
    }

    public List<LightMakeup> getLight_makeup(){
        return light_makeup;
    }

    public LightMakeupConfig(List<LightMakeup> light_makeup) {
        this.light_makeup = light_makeup;
    }

    public void setLight_makeup(List<LightMakeup> light_makeup) { this.light_makeup = light_makeup;}

    public static class LightMakeup {

        public static final LightMakeup NO_STYLE = new LightMakeup("","","", "", "", 2);

        public LightMakeup(String title,String titleEn, String name, String category, String icon, int download) {
            this.title = title;
            this.title_en = titleEn;
            this.name = name;
            this.category = category;
            this.icon = icon;
            this.download = download;
        }
        @Override public String toString() {
            return "LightMakeup{" +
                    "name='" + name + '\'' +
                    ", category='" + category + '\'' +
                    ", icon='" + icon + '\'' +
                    ", download=" + download +
                    '}';
        }

        /**
         * title
         */
        private String title;
        private String title_en;
        /**
         * name
         */
        private String name;
        /**
         * category
         */
        private String category;
        /**
         * icon
         */
        private String icon;
        /**
         * downloaded
         */
        private int download;


        public String getName() { return name;}

        public void setName(String name) { this.name = name;}

        public String getCategory() { return category;}

        public void setCategory(String category) { this.category = category;}

        public String getIcon() {
            Log.i("gao", "getIcon: "+FBEffect.shareInstance().getStylePath() + File.separator + this.name + ".png");
            return FBEffect.shareInstance().getStylePath() + File.separator + this.name + ".png";
        }

        public void setThumb(String icon) { this.icon = icon;}

        public int isDownloaded() { return download;}

        public void setDownloaded(int download) {
            this.download = download;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitleEn() {
            return title_en;
        }

        public void setTitleEn(String titleEn) {
            this.title_en = titleEn;
        }


        /**
         * 下载完成设置缓存文件
         */
  /*  public void downloaded() {
      LightMakeupConfig makeupstyleList = HtConfigTools.getInstance().getMakeupStyleList();
      for (LightMakeup makeup : makeupstyleList.getStyles()) {
        if (makeup.name.equals(this.name) && makeup.type.equals(this.type)) {
          makeup.setDownloaded(true);
        }
      }
      HtConfigTools.getInstance().makeupStyleDownLoad(new Gson().toJson(makeupstyleList));
    }*/


    }

}
