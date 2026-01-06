package com.nimo.fb_effect.model;

import com.google.gson.Gson;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import com.nimo.fb_effect.utils.FBConfigTools;

import java.io.File;
import java.util.List;

public class FBHairConfig {

    /**
     * hairstyling
     */
    private List<FBHair> hair_style_list;

    @Override public String toString() {
        return "FBHairConfig{" +
                "fbHairs=" + hair_style_list.size() +
                "个}";
    }

    public List<FBHair> getHairs() {
        return hair_style_list;
    }

    public FBHairConfig(List<FBHair> hairs) {
        this.hair_style_list = hairs;
    }

    public void setHairs(List<FBHair> tiHairs) { this.hair_style_list = tiHairs;}

    public static class FBHair {

        public static final FBHairConfig.FBHair NO_HAIR = new FBHairConfig.FBHair("", "", "",0,"","", FBDownloadState.COMPLETE_DOWNLOAD);
        public static final FBHairConfig.FBHair NEW_HAIR = new FBHairConfig.FBHair("", "", "", 0,"","",FBDownloadState.COMPLETE_DOWNLOAD);

        /**
         * name
         */
        private String name;


        public FBHair(String title,String titleEn,String name, int id,String category, String icon, int downloaded) {
            this.title = title;
            this.title_en = titleEn;
            this.name = name;
            this.category = category;
            this.id = id;
            this.icon = icon;
            this.download = downloaded;
        }

        @Override public String toString() {
            return "FBHair{" +
                    "name='" + name + '\'' +
                    ", category='" + category + '\'' +
                    ", id='" + id + '\'' +
                    ", icon='" + icon + '\'' +
                    ", downloaded=" + download +
                    '}';
        }

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
        private String title;
        private int id;
        private String title_en;
        public String getUrl() {
            return FBEffect.shareInstance().getFilterUrl() + name + ".zip";

        }

        public String getName() { return name;}

        public void setName(String name) { this.name = name;}
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCategory() { return category;}

        public void setCategory(String category) { this.category = category;}

        public String getIcon() {
            return FBEffect.shareInstance().getARItemPathBy(5) + File.separator + this.name + ".png";
           /* //todo 等待接口
            return FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemMask.getValue()) + "/ICON/" + this.icon;*/
//      return FBEffect.shareInstance().getARItemUrlBy(FBItemEnum.FBItemMask.getValue()) + icon;


        }

        public void setThumb(String icon) { this.icon = icon;}

        public int isDownloaded() { return download;}

        public void setDownloaded(int downloaded) {
            this.download = downloaded;
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
         * 下载完成更新缓存数据
         */
        public void downloaded() {
            FBHairConfig tiHairConfig = FBConfigTools.getInstance().getHairList();

            for (FBHairConfig.FBHair hair : tiHairConfig.getHairs()) {
                if (this.name.equals(hair.name) && hair.icon.equals(this.icon)) {
                    hair.setDownloaded(FBDownloadState.COMPLETE_DOWNLOAD);
                }
            }
            FBConfigTools.getInstance().hairDownload(new Gson().toJson(tiHairConfig));
        }

    }

}
