package com.nimo.fb_effect.model;


import com.nimo.facebeauty.FBEffect;

import java.io.File;
import java.util.List;

/**
 * 特效滤镜配置
 */
@SuppressWarnings("unused")
public class FBFunnyFilterConfig {

  /**
   * stickers
   */
  private List<FBFunnyFilter> funny_filter;

  @Override public String toString() {
    return "FBFunnyFilterConfig{" +
        "fbFilters=" + funny_filter.size() +
        "个}";
  }

  public List<FBFunnyFilter> getFilters() {
    return funny_filter;
  }

  public FBFunnyFilterConfig(List<FBFunnyFilter> filters) {
    this.funny_filter = filters;
  }

  public void setFilters(List<FBFunnyFilter> fbFilters) { this.funny_filter = fbFilters;}

  public static class FBFunnyFilter {

    public static final FBFunnyFilter NO_FILTER = new FBFunnyFilter("","","", "", "", 2);

    public FBFunnyFilter(String title, String titleEn, String name, String category, String icon, int download) {
      this.title = title;
      this.title_en = titleEn;
      this.name = name;
      this.category = category;
      this.icon = icon;
      this.download = download;
    }

    @Override public String toString() {
      return "HtFunnyFilterConfig{" +
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

    public String getUrl() {

      return FBEffect.shareInstance().getFilterUrl() + name + ".zip";

    }

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getCategory() { return category;}

    public void setCategory(String category) { this.category = category;}

    public String getIcon() {
      return FBEffect.shareInstance().getFilterPath() + File.separator + this.name + ".png";
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
     * 下载完成更新缓存数据
     */
    // public void downloaded() {
    //   HtEffectFilterConfig fbFilterConfig = HtConfigTools.getInstance().getFilterConfig();
    //
    //   for (HtFilter filter : fbFilterConfig.getFilters()) {
    //     if (this.name.equals(filter.name) && filter.icon.equals(this.icon)) {
    //       filter.setDownloaded(2);
    //     }
    //   }
    //   HtConfigTools.getInstance().filterDownload(new Gson().toJson(fbFilterConfig));
    // }

  }
}

