package com.nimo.fb_effect.model;

import com.google.gson.Gson;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;

import java.io.File;
import java.util.List;

/**
 * 贴纸列表配置
 */
@SuppressWarnings("unused")
public class FBStickerConfig {

  /**
   * stickers
   */
  private List<FBSticker> fb_sticker;

  @Override public String toString() {
    return "FBStickerConfig{" +
        "fbStickers=" + fb_sticker.size() +
        "个}";
  }

  public List<FBSticker> getStickers() {
    return fb_sticker;
  }

  public FBStickerConfig(List<FBSticker> stickers) {
    this.fb_sticker = stickers;
  }

  public void setStickers(List<FBSticker> tiStickers) { this.fb_sticker = tiStickers;}

  public static class FBSticker {

    public static final FBSticker NO_STICKER = new FBSticker("", "", "", FBDownloadState.COMPLETE_DOWNLOAD);
    public static final FBSticker NEW_STICKER = new FBSticker("", "", "", FBDownloadState.COMPLETE_DOWNLOAD);

    /**
     * name
     */
    private String name;


    public FBSticker(String name, String category, String icon, int downloaded) {
      this.name = name;
      this.category = category;
      this.icon = icon;
      this.download = downloaded;
    }

    @Override public String toString() {
      return "FBSticker{" +
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

      return FBEffect.shareInstance().getARItemUrlBy(FBItemEnum.FBItemSticker.getValue()) + name + ".zip";

    }

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getCategory() { return category;}

    public void setCategory(String category) { this.category = category;}

    public String getIcon() {
//      return FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemSticker.getValue()) + "/ICON/" + this.icon;
      return FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemSticker.getValue()) + File.separator + this.name + ".png";

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
      FBStickerConfig tiStickerConfig = FBConfigTools.getInstance().getStickerList();

      for (FBSticker sticker : tiStickerConfig.getStickers()) {
        if (this.name.equals(sticker.name) && sticker.icon.equals(this.icon)) {
          sticker.setDownloaded(FBDownloadState.COMPLETE_DOWNLOAD);
        }
      }
      FBConfigTools.getInstance().stickerDownload(new Gson().toJson(tiStickerConfig));
    }

  }

}
