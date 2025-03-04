package com.nimo.fb_effect.model;

import com.google.gson.Gson;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import java.util.List;

/**
 * 贴纸列表配置
 */
@SuppressWarnings("unused")
public class FBMaskConfig {

  /**
   * stickers
   */
  private List<FBMask> fb_mask;

  @Override public String toString() {
    return "FBMaskConfig{" +
        "fbMasks=" + fb_mask.size() +
        "个}";
  }

  public List<FBMask> getMasks() {
    return fb_mask;
  }

  public FBMaskConfig(List<FBMask> masks) {
    this.fb_mask = masks;
  }

  public void setMasks(List<FBMask> tiMasks) { this.fb_mask = tiMasks;}

  public static class FBMask {

    public static final FBMask NO_MASK = new FBMask("", "", "", FBDownloadState.COMPLETE_DOWNLOAD);
    public static final FBMask NEW_MASK = new FBMask("", "", "", FBDownloadState.COMPLETE_DOWNLOAD);

    /**
     * name
     */
    private String name;


    public FBMask(String name, String category, String icon, int downloaded) {
      this.name = name;
      this.category = category;
      this.icon = icon;
      this.download = downloaded;
    }

    @Override public String toString() {
      return "FBMask{" +
          "name='" + name + '\'' +
          ", category='" + category + '\'' +
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

    public String getUrl() {

      return FBEffect.shareInstance().getARItemUrlBy(FBItemEnum.FBItemMask.getValue()) + name + ".zip";

    }

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getCategory() { return category;}

    public void setCategory(String category) { this.category = category;}

    public String getIcon() {
      //todo 等待接口
      return FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemMask.getValue()) + "/ICON/" + this.icon;
//      return FBEffect.shareInstance().getARItemUrlBy(FBItemEnum.FBItemMask.getValue()) + icon;


    }

    public void setThumb(String icon) { this.icon = icon;}

    public int isDownloaded() { return download;}

    public void setDownloaded(int downloaded) {
      this.download = downloaded;
    }

    /**
     * 下载完成更新缓存数据
     */
    public void downloaded() {
      FBMaskConfig tiMaskConfig = FBConfigTools.getInstance().getMaskList();

      for (FBMask mask : tiMaskConfig.getMasks()) {
        if (this.name.equals(mask.name) && mask.icon.equals(this.icon)) {
          mask.setDownloaded(FBDownloadState.COMPLETE_DOWNLOAD);
        }
      }
      FBConfigTools.getInstance().maskDownload(new Gson().toJson(tiMaskConfig));
    }

  }

}
