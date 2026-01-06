package com.nimo.fb_effect.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.fb_effect.utils.FileUtils;


import java.io.File;
import java.util.List;

/**
 * 水印配置参数
 */
public class FBWatermarkConfig {
  /**
   * watermarks
   */
  private List<FBWatermark> watermark;

  public List<FBWatermark> getWatermarks() { return watermark;}

  public void setWatermarks(List<FBWatermark> fbWatermarks) { this.watermark = fbWatermarks;}

  public static class FBWatermark {

    public static final FBWatermark NO_WATERMARK =
        new FBWatermark("", "", FBDownloadState.COMPLETE_DOWNLOAD,""
        );

    public FBWatermark(String name, String icon, int download, String dir) {

      this.name = name;
      this.icon = icon;
      this.download = download;
      this.dir = dir;
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
    private String dir;

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getIcon() {
      return FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemWatermark.getValue()) + File.separator + this.name + File.separator + this.name + ".png";
    }

    public void setIcon(String icon) { this.icon = icon;}

    public int isDownloaded() { return download;}

    public void setDownloaded(int download) { this.download = download;}

    public String getUrl() {
      return FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemWatermark.getValue()) + File.separator + this.name + File.separator + this.name + ".png";

    }

    public String getDir() {
      return dir;
    }


    public void setDir(String dir) {
      this.dir = dir;
    }

    /**
     * 下载完更新缓存
     */
    public void downloaded() {
      FBWatermarkConfig watermarkList = FBConfigTools.getInstance().getWatermarkList();

      for (FBWatermark watermark : watermarkList.watermark) {
        if (watermark.name == this.name) {
          watermark.setDownloaded(FBDownloadState.COMPLETE_DOWNLOAD);
        }
      }

      FBConfigTools.getInstance().watermarkDownload(new Gson().toJson(watermarkList));

    }

    public void delete(int position) {
      FBWatermarkConfig watermarkList = FBConfigTools.getInstance().getWatermarkList();
      watermarkList.getWatermarks().remove(position);
      FBConfigTools.getInstance().watermarkDownload(new Gson().toJson(watermarkList));
    }
    private boolean isFromDisk = false;
    public boolean isFromDisk() {
      return isFromDisk;
    }

    public void setFromDisk(boolean fromDisk, Context context, final String sourcePath) {
      isFromDisk = fromDisk;
      if (isFromDisk) {
        FBWatermarkConfig watermarkList = FBConfigTools.getInstance().getWatermarkList();
        if (watermarkList != null) {
          Log.i("添加图片：", sourcePath);
          //根据地址读取源文件
          File sourceFile = new File(sourcePath);
          //获取最后一个.的位置
          int lastIndexOf = sourcePath.lastIndexOf(".");
          //获取文件的后缀名 .jpg
          String suffix = sourcePath.substring(lastIndexOf);
          //拼接出新的文件名
          String newFileName = (System.currentTimeMillis() / 1000) + "";
          //目标位置和文件
          File targetFile = new File(FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemWatermark.getValue())+"/watermark_icon", newFileName + suffix);
          File targetDirectory = new File(FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemWatermark.getValue()) + "/"+newFileName + "/"+newFileName + suffix);
          //文件复制

          if (FileUtils.copyFile(sourceFile, targetFile)) {
            Log.i("复制绿幕背景文件：", "成功");
            this.dir = targetFile.getAbsolutePath();
          } else {
            Log.e("复制绿幕背景文件：", "失败");
            return;
          }
          if (FileUtils.copyFile(sourceFile, targetDirectory)) {
            Log.i("复制绿幕背景文件：", "成功");
          } else {
            Log.e("复制绿幕背景文件：", "失败");
            return;
          }
          //更新配置文件的名称
          this.name = newFileName;

          //持久化
          watermarkList.getWatermarks().add(this);
          FBConfigTools.getInstance().watermarkDownload(new Gson().toJson(watermarkList));
        }
      }
    }

  }

}
