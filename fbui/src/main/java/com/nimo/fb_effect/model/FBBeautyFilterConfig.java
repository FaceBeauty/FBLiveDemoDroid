package com.nimo.fb_effect.model;

import com.nimo.facebeauty.FBEffect;
import java.io.File;
import java.util.List;

/**
 * 风格滤镜列表配置
 */
@SuppressWarnings("unused")
public class FBBeautyFilterConfig {

  /**
   * stickers
   */
  private List<FBBeautyFilter> fb_style_filter;

  @Override public String toString() {
    return "FBStyleFilterConfig{" +
        "fbFilters=" + fb_style_filter.size() +
        "个}";
  }

  public List<FBBeautyFilter> getFilters() {
    return fb_style_filter;
  }

  public FBBeautyFilterConfig(List<FBBeautyFilter> filters) {
    this.fb_style_filter = filters;
  }

  public void setFilters(List<FBBeautyFilter> fbFilters) { this.fb_style_filter = fbFilters;}

  public static class FBBeautyFilter {

    public static final FBBeautyFilter NO_FILTER = new FBBeautyFilter("","","", "", "", 2);

    public FBBeautyFilter(String title,String titleEn, String name, String category, String icon, int download) {
      this.title = title;
      this.title_en = titleEn;
      this.name = name;
      this.category = category;
      this.icon = icon;
      this.download = download;
    }

    @Override public String toString() {
      return "FBStyleFilter{" +
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


  }
}

