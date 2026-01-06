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
import com.nimo.fb_effect.model.FBEffectFilterConfig;
import com.nimo.fb_effect.model.FBFunnyFilterConfig;
import com.nimo.fb_effect.model.FBHairConfig;
import com.nimo.fb_effect.model.FBMaskConfig;
import com.nimo.fb_effect.model.FBStickerConfig;
import com.nimo.fb_effect.model.FBBeautyFilterConfig;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import com.nimo.fb_effect.model.FBWatermarkConfig;

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
  //面具配置的文件路径
  private String PATH_HAIR;

  //风格滤镜配置文件
  private String PATH_BEAUTY_FILTER;
  //哈哈镜配置文件
  private String PATH_FUNNY_FILTER;
  //水印配置文件
  private String PATH_WATER_MARK;
  //特效滤镜配置文件
  private String PATH_EFFECT_FILTER;
  private FBStickerConfig stickerList;
  private FBFunnyFilterConfig funnyFilterList;
  private FBWatermarkConfig watermarkList;
  private FBMaskConfig maskList;
  private FBHairConfig hairList;
  private FBEffectFilterConfig effectFilterList;//特效滤镜
  private FBBeautyFilterConfig beautyFilterList;

  private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

  final Handler uiHandler = new Handler(Looper.getMainLooper());

  @SuppressLint("StaticFieldLeak")
  private static FBConfigTools instance;

  public void initFBConfigTools(Context context) {
    this.context = context;
    instance = this;

    //贴纸配置的文件路径
    PATH_STICKER = FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemSticker.getValue()) + File.separator + "sticker_config.json";
    //面具配置的文件路径
    PATH_MASK = FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemMask.getValue()) + File.separator + "mask_config.json";
    //美发配置的文件路径
    PATH_HAIR = context.getFilesDir().getAbsolutePath()+ "/fbeffect/hair/hair_config.json";

    //滤镜配置文件
    PATH_BEAUTY_FILTER = FBEffect.shareInstance().getFilterPath() + File.separator + "style_filter_config.json";
    //哈哈镜
    PATH_FUNNY_FILTER = FBEffect.shareInstance().getFilterPath() + File.separator + "funny_filter_config.json";
    //特效滤镜
    PATH_EFFECT_FILTER = FBEffect.shareInstance().getFilterPath() + File.separator + "effect_filter_config.json";
    //水印配置文件
    PATH_WATER_MARK = FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemWatermark.getValue()) + File.separator + "watermark_config.json";
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
  public FBHairConfig getHairList() {
    if (hairList == null) return null;
    return hairList;
  }
  public FBWatermarkConfig getWatermarkList() {
    if (watermarkList == null) return null;
    return watermarkList;
  }
  public FBFunnyFilterConfig getFunnyFilterConfig() {
    if (funnyFilterList == null) return null;
    return funnyFilterList;
  }
  public FBBeautyFilterConfig getBeautyFilterConfig() {
    if (beautyFilterList == null) return null;
    return beautyFilterList;
  }

  /**
   * 特效滤镜
   * @return
   */
  public FBEffectFilterConfig getEffectFilterConfig() {
    if (effectFilterList == null) return null;
    return effectFilterList;
  }
  /**
   * 获取缓存文件中特效滤镜配置
   */
  public void getEffectFiltersConfig(FBConfigCallBack<List<FBEffectFilterConfig.FBEffectFilter>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String res = getFileString(PATH_EFFECT_FILTER);
          if (TextUtils.isEmpty(res)) {
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });
          } else {
            effectFilterList = new Gson().fromJson(res, new TypeToken<FBEffectFilterConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(effectFilterList.getFilters());
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
   * 更新特效滤镜文件
   */
  public void effectFilterDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_EFFECT_FILTER);
      }
    });
  }
  /**
   * 从缓存文件中获取水印配置文件
   */
  public void getWatermarksConfig(FBConfigCallBack<List<FBWatermarkConfig.FBWatermark>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String result = getFileString(PATH_WATER_MARK);

          if (TextUtils.isEmpty(result)) {
            Log.i("读取绿幕配置文件：", "内容为空");
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });

          } else {
            watermarkList = new Gson().fromJson(result, new TypeToken<FBWatermarkConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(watermarkList.getWatermarks());
              }
            });
          }

        } catch (IOException e) {
          e.printStackTrace();
          callBack.fail(e);
        }
      }
    });
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
   * 更新水印缓存文件
   */
  public void watermarkDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_WATER_MARK);
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
   * 获取缓存文件中美发配置
   */
  public void getHairsConfig(FBConfigCallBack<List<FBHairConfig.FBHair>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String res = getFileString(PATH_HAIR);
          if (TextUtils.isEmpty(res)) {
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });
          } else {
            hairList = new Gson().fromJson(res, new TypeToken<FBHairConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(hairList.getHairs());
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
   * 获取缓存文件中哈哈镜配置
   */
  public void getFunnyFiltersConfig(FBConfigCallBack<List<FBFunnyFilterConfig.FBFunnyFilter>> callBack) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        try {
          String res = getFileString(PATH_FUNNY_FILTER);
          if (TextUtils.isEmpty(res)) {
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(new ArrayList<>());
              }
            });
          } else {
            funnyFilterList = new Gson().fromJson(res, new TypeToken<FBFunnyFilterConfig>() {}.getType());
            uiHandler.post(new Runnable() {
              @Override public void run() {
                callBack.success(funnyFilterList.getFilters());
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
   * 更新哈哈镜文件
   */
  public void hahaFilterDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_FUNNY_FILTER);
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
   * 更新美发文件
   */
  public void hairDownload(String content) {
    cachedThreadPool.execute(new Runnable() {
      @Override public void run() {
        modifyFile(content, PATH_HAIR);
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
          String newSticker = getJsonString(context, "sticker/sticker_config.json");
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
        String newWatermark;
        try {
          newWatermark = getJsonString(context, "watermark/watermarks.json");
          modifyFile(newWatermark, PATH_WATER_MARK);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

}

