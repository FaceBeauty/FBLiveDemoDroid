package com.nimo.fb_effect.model;


import com.nimo.facebeauty.FBEffect;

import java.util.List;

/**
 * 特效滤镜配置
 */
@SuppressWarnings("unused")
public class FBEffectFilterConfig {

  /**
   * stickers
   */
  private List<FBEffectFilter> effect_filter;

  @Override public String toString() {
    return "FBEffectFilterConfig{" +
        "fbFilters=" + effect_filter.size() +
        "个}";
  }

  public List<FBEffectFilter> getFilters() {
    return effect_filter;
  }

  public FBEffectFilterConfig(List<FBEffectFilter> filters) {
    this.effect_filter = filters;
  }

  public void setFilters(List<FBEffectFilter> fbFilters) { this.effect_filter = fbFilters;}

  public static class FBEffectFilter {

    public static final FBEffectFilter NO_FILTER = new FBEffectFilter("","","", "", "", 2);

    public FBEffectFilter(String title,String titleEn, String name, String category, String icon, int download) {
      this.title = title;
      this.title_en = titleEn;
      this.name = name;
      this.category = category;
      this.icon = icon;
      this.download = download;
    }

    @Override public String toString() {
      return "FBEffectFilter{" +
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
      return this.icon;
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
    //   FBEffectFilterConfig htFilterConfig = HtConfigTools.getInstance().getFilterConfig();
    //
    //   for (HtFilter filter : htFilterConfig.getFilters()) {
    //     if (this.name.equals(filter.name) && filter.icon.equals(this.icon)) {
    //       filter.setDownloaded(2);
    //     }
    //   }
    //   HtConfigTools.getInstance().filterDownload(new Gson().toJson(htFilterConfig));
    // }

  }
}

