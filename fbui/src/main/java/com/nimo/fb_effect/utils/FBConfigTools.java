package com.nimo.fb_effect.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimo.fb_effect.model.FBMaskConfig;
import com.nimo.fb_effect.model.FBStickerConfig;
import com.nimo.fb_effect.model.FBBeautyFilterConfig;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class FBConfigTools {

  private Context context;

  private int makeupType;

  //贴纸配置的文件路径
  private String PATH_STICKER;
  //面具配置的文件路径
  private String PATH_MASK;

  //风格滤镜配置文件
  private String PATH_BEAUTY_FILTER;

  private FBStickerConfig stickerList;
  private FBMaskConfig maskList;
  private FBBeautyFilterConfig beautyFilterList;

  private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

  final Handler uiHandler = new Handler(Looper.getMainLooper());

  @SuppressLint("StaticFieldLeak")
  private static FBConfigTools instance;

  public void initFBConfigTools(Context context) {
    this.context = context;
    instance = this;

    //贴纸配置的文件路径
    PATH_STICKER = FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemSticker.getValue()) + File.separator + "fb_sticker_config.json";
    //面具配置的文件路径
    PATH_MASK = FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemMask.getValue()) + File.separator + "fb_mask_config.json";

    //滤镜配置文件
    PATH_BEAUTY_FILTER = FBEffect.shareInstance().getFilterPath() + File.separator + "fb_style_filter_config.json";

  }

  public static FBConfigTools getInstance() {
    if (instance == null) instance = new FBConfigTools();
    return instance;
  }


  public FBStickerConfig getStickerList() {
    if (stickerList == null) return null;
    return stickerList;
  }

  public FBMaskConfig getMaskList() {
    if (maskList == null) return null;
    return maskList;
  }


  public FBBeautyFilterConfig getBeautyFilterConfig() {
    if (beautyFilterList == null) return null;
    return beautyFilterList;
  }

  /**
   * 获取缓存文件中贴纸配置
   */
  public void getStickersConfig(FBConfigCallBack<List<FBStickerConfig.FBSticker>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String res = getFileString(PATH_STICKER);
          if (TextUtils.isEmpty(res)) {
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });
          } else {
            stickerList = new Gson().fromJson(res, new TypeToken<FBStickerConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(stickerList.getStickers());
              }
            });
          }

        } catch (Exception e) {
          uiHandler.post(new Runnable() {
            @Override public void run() {
              callBack.fail(e);
            }
          });
        }
      }
    });
  }

  /**
   * 更新贴纸文件
   */
  public void stickerDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_STICKER);
      }
    });
  }

  /**
   * 获取缓存文件中面具配置
   */
  public void getMasksConfig(FBConfigCallBack<List<FBMaskConfig.FBMask>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String res = getFileString(PATH_MASK);
          if (TextUtils.isEmpty(res)) {
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });
          } else {
            maskList = new Gson().fromJson(res, new TypeToken<FBMaskConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(maskList.getMasks());
              }
            });
          }

        } catch (Exception e) {
          uiHandler.post(new Runnable() {
            @Override public void run() {
              callBack.fail(e);
            }
          });
        }
      }
    });
  }

  /**
   * 更新mask文件
   *
   * @param content json 内容
   */
  public void maskDownload(final String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_MASK);
      }
    });
  }


  /**
   * 获取缓存文件中风格滤镜配置
   */
  public void getStyleFiltersConfig(FBConfigCallBack<List<FBBeautyFilterConfig.FBBeautyFilter>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String res = getFileString(PATH_BEAUTY_FILTER);
          if (TextUtils.isEmpty(res)) {
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });
          } else {
            beautyFilterList = new Gson().fromJson(res, new TypeToken<FBBeautyFilterConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(beautyFilterList.getFilters());
              }
            });
          }

        } catch (Exception e) {
          uiHandler.post(new Runnable() {
            @Override public void run() {
              callBack.fail(e);
            }
          });
        }
      }
    });
  }
  /**
   * 更新风格滤镜文件
   */
  public void styleFilterDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_BEAUTY_FILTER);
      }
    });
  }

  /**
   * 写入文件
   *
   * @param content 内容
   * @param filePath 地址
   */
  private void modifyFile(String content, String filePath) {
    try {
      FileWriter fileWriter = new FileWriter(filePath, false);
      BufferedWriter writer = new BufferedWriter(fileWriter);
      writer.append(content);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 读取assets下配置文件
   *
   * @param context 上下文
   * @param fileName 文件名
   * @return 内容
   */
  private String getJsonString(Context context, String fileName)
      throws IOException {
    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();
    try {
      AssetManager manager = context.getAssets();
      br = new BufferedReader(new InputStreamReader(manager.open(fileName)));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return sb.toString();
  }

  public void release() {
    this.context = null;
  }

  /**
   * 获取指定目录下的字符内容
   *
   * @param filePath 路径
   * @return 字符内容
   */
  private String getFileString(String filePath) throws IOException {

    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();
    try {
      File file = new File(filePath);
      FileInputStream fis = new FileInputStream(file);
      br = new BufferedReader(new InputStreamReader(fis));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      //            return sb.toString();
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return sb.toString();
  }

  /**
   * 重置配置文件
   */
  public void resetConfigFile() {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String newSticker = getJsonString(context, "sticker/fb_sticker_config.json");
          modifyFile(newSticker, PATH_STICKER);
        } catch (IOException e) {
          e.printStackTrace();
        }

        String newMask;
        try {
          newMask = getJsonString(context, "mask/masks.json");
          modifyFile(newMask, PATH_MASK);
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    });
  }

}

