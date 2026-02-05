package com.nimo.fb_effect.model;

import android.util.Log;

import com.google.gson.Gson;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import com.nimo.fb_effect.utils.FBConfigTools;

import java.util.List;

/**
 * 礼物配置参数
 */
public class GiftConfig {
  /**
   * watermarks
   */
  private List<FbGift> gift;

  public List<FbGift> getGifts() { return gift;}

  public void setGifts(List<FbGift> fbGifts) { this.gift = fbGifts;}

  public static class FbGift {

    public static final FbGift NO_Gift =
        new FbGift("", "", FBDownloadState.COMPLETE_DOWNLOAD
        );
    public static final FbGift NEW_Gift =
        new FbGift("", "", FBDownloadState.COMPLETE_DOWNLOAD
        );

    public FbGift(String name, String icon, int download) {

      this.name = name;
      this.icon = icon;
      this.download = download;
    }

    /**
     * name
     */
    private String name;
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

    public String getIcon() {
        Log.i("gao", "getIcon: "+FBEffect.shareInstance().getARItemUrlBy(FBItemEnum.FBItemGift.getValue()) + icon);
      return FBEffect.shareInstance().getARItemUrlBy(FBItemEnum.FBItemGift.getValue()) + icon;
    }

    public void setIcon(String icon) { this.icon = icon;}

    public int isDownloaded() { return download;}

    public void setDownloaded(int download) { this.download = download;}

    public String getUrl() {
      return FBEffect.shareInstance().getARItemUrlBy(FBItemEnum.FBItemGift.getValue()) + name + ".zip";

    }

    /**
     * 下载完更新缓存
     */
    public void downloaded() {
      GiftConfig giftList = FBConfigTools.getInstance().getGiftList();

      for (FbGift gift : giftList.gift) {
        if (gift.name == this.name) {
          gift.setDownloaded(FBDownloadState.COMPLETE_DOWNLOAD);
        }
      }

      FBConfigTools.getInstance().giftDownload(new Gson().toJson(giftList));

    }

  }

}
