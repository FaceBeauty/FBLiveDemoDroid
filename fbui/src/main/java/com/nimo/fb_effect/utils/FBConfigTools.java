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
import com.nimo.facebeauty.model.FBMakeupEnum;
import com.nimo.fb_effect.model.BlushConfig;
import com.nimo.fb_effect.model.EyebrowConfig;
import com.nimo.fb_effect.model.EyelashConfig;
import com.nimo.fb_effect.model.EyelineConfig;
import com.nimo.fb_effect.model.EyeshadowConfig;
import com.nimo.fb_effect.model.FBEffectFilterConfig;
import com.nimo.fb_effect.model.FBFunnyFilterConfig;
import com.nimo.fb_effect.model.FBHairConfig;
import com.nimo.fb_effect.model.FBMaskConfig;
import com.nimo.fb_effect.model.FBStickerConfig;
import com.nimo.fb_effect.model.FBBeautyFilterConfig;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import com.nimo.fb_effect.model.FBWatermarkConfig;
import com.nimo.fb_effect.model.GiftConfig;
import com.nimo.fb_effect.model.LightMakeupConfig;
import com.nimo.fb_effect.model.LipstickConfig;
import com.nimo.fb_effect.model.Makeup;
import com.nimo.fb_effect.model.PupilsConfig;

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
  private String PATH_GIFT;
    //轻美妆配置的文件路径
  private String PATH_LIGHT_MAKEUP;

  //风格滤镜配置文件
  private String PATH_BEAUTY_FILTER;
  //哈哈镜配置文件
  private String PATH_FUNNY_FILTER;
  //水印配置文件
  private String PATH_WATER_MARK;
  //特效滤镜配置文件
  private String PATH_EFFECT_FILTER;
    //口红配置文件
  private String PATH_LIPSTICK;
    //眉毛配置文件
    private String PATH_EYEBROW;
    //腮红配置文件
    private String PATH_BLUSH;
    //眼影配置文件
    private String PATH_EYESHADOW;
    //眼线配置文件
    private String PATH_EYELINE;
    //睫毛配置文件
    private String PATH_EYELASH;
    //美瞳配置文件
    private String PATH_PUPILS;

    private FBStickerConfig stickerList;
  private FBFunnyFilterConfig funnyFilterList;
  private FBWatermarkConfig watermarkList;
  private FBMaskConfig maskList;
  private FBHairConfig hairList;
  private FBEffectFilterConfig effectFilterList;//特效滤镜
  private FBBeautyFilterConfig beautyFilterList;
  private LightMakeupConfig lightMakeupList;//轻彩妆
  private LipstickConfig lipstickList;//口红
  private EyebrowConfig eyebrowList;
    private BlushConfig blushList;
    private EyeshadowConfig eyeshadowList;
    private EyelineConfig eyelineList;
    private EyelashConfig eyelashList;
    private PupilsConfig pupilsList;
    private GiftConfig giftList;

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
    PATH_HAIR = context.getFilesDir().getAbsolutePath()+ "/fbeffect/hairstyling/hairstyling_config.json";
      //轻美妆配置的文件路径
      PATH_LIGHT_MAKEUP =  FBEffect.shareInstance().getStylePath() + File.separator + "light_makeup_config.json";
    //滤镜配置文件
    PATH_BEAUTY_FILTER = FBEffect.shareInstance().getFilterPath() + File.separator + "style_filter_config.json";
    //哈哈镜
    PATH_FUNNY_FILTER = FBEffect.shareInstance().getFilterPath() + File.separator + "funny_filter_config.json";
    //特效滤镜
    PATH_EFFECT_FILTER = FBEffect.shareInstance().getFilterPath() + File.separator + "effect_filter_config.json";
    //水印配置文件
    PATH_WATER_MARK = FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemWatermark.getValue()) + File.separator + "watermark_config.json";
    //口红配置文件
      PATH_LIPSTICK = FBEffect.shareInstance().getMakeupPath(FBMakeupEnum.HTMakeupLipstick.getValue()) + File.separator + "makeup_lipstick_config.json";
      PATH_EYEBROW = FBEffect.shareInstance().getMakeupPath(FBMakeupEnum.HTMakeupEyebrow.getValue()) + File.separator + "makeup_eyebrow_config.json";
      PATH_BLUSH = FBEffect.shareInstance().getMakeupPath(FBMakeupEnum.HTMakeupBlush.getValue()) + File.separator + "makeup_blush_config.json";
      PATH_EYESHADOW = FBEffect.shareInstance().getMakeupPath(FBMakeupEnum.HTMakeupEyeshadow.getValue()) + File.separator + "makeup_eyeshadow_config.json";
      PATH_EYELINE = FBEffect.shareInstance().getMakeupPath(FBMakeupEnum.HTMakeupEyeline.getValue()) + File.separator + "makeup_eyeline_config.json";
      PATH_EYELASH = FBEffect.shareInstance().getMakeupPath(FBMakeupEnum.HTMakeupEyelash.getValue()) + File.separator + "makeup_eyelash_config.json";
      PATH_PUPILS = FBEffect.shareInstance().getMakeupPath(FBMakeupEnum.HTMakeupPupils.getValue()) + File.separator + "makeup_pupils_config.json";

      //礼物配置的文件路径
      PATH_GIFT = FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemGift.getValue()) + File.separator + "gift_config.json";
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
    public LightMakeupConfig getLightMakeupList() {
        if (lightMakeupList == null) return null;
        return lightMakeupList;
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
    public LipstickConfig getLipstickList() {
        if (lipstickList == null) return null;
        return lipstickList;
    }

    public EyebrowConfig getEyebrowList() {
        if (eyebrowList == null) return null;
        return eyebrowList;
    }

    public BlushConfig getBlushList() {
        if (blushList == null) return null;
        return blushList;
    }

    public EyeshadowConfig getEyeshadowList() {
        if (eyeshadowList == null) return null;
        return eyeshadowList;
    }

    public EyelineConfig getEyelineList() {
        if (eyelineList == null) return null;
        return eyelineList;
    }

    public EyelashConfig getEyelashList() {
        if (eyelashList == null) return null;
        return eyelashList;
    }

    public PupilsConfig getPupilsList() {
        if (pupilsList == null) return null;
        return pupilsList;
    }
    public GiftConfig getGiftList() {
        if (giftList == null) return null;
        return giftList;
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
     * 获取缓存文件中礼物配置
     */
    public void getGiftsConfig(FBConfigCallBack<List<GiftConfig.FbGift>> callBack) {
        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                try {
                    String res = getFileString(PATH_GIFT);
                    if (TextUtils.isEmpty(res)) {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(new ArrayList<>());
                            }
                        });
                    } else {
                        giftList = new Gson().fromJson(res, new TypeToken<GiftConfig>() {}.getType());
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(giftList.getGifts());
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
     * 获取缓存文件中口红配置
     */
    public void getLipsticksConfig(FBConfigCallBack<List<Makeup>> callBack) {

        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                String res;
                try {
                    res = getFileString(PATH_LIPSTICK);
                    if (TextUtils.isEmpty(res)) {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(new ArrayList<>());
                            }
                        });
                    } else {
                        lipstickList = new Gson().fromJson(res, new TypeToken<LipstickConfig>() {}.getType());
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(lipstickList.getLipsticks());
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
     * 获取缓存文件中眉毛配置
     */
    public void getEyebrowsConfig(FBConfigCallBack<List<Makeup>> callBack) {
        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                String res;
                try {
                    res = getFileString(PATH_EYEBROW);

                    if (TextUtils.isEmpty(res)) {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(new ArrayList<>());
                            }
                        });
                    } else {
                        eyebrowList = new Gson().fromJson(res, new TypeToken<EyebrowConfig>() {}.getType());
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(eyebrowList.getEyebrows());
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
     * 获取缓存文件中腮红配置
     */
    public void getBlushsConfig(FBConfigCallBack<List<Makeup>> callBack) {

        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                String res;
                try {
                    res = getFileString(PATH_BLUSH);
                    if (TextUtils.isEmpty(res)) {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(new ArrayList<>());
                            }
                        });
                    } else {
                        blushList = new Gson().fromJson(res, new TypeToken<BlushConfig>() {}.getType());
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(blushList.getBlushes());
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
     * 获取缓存文件中眼影配置
     */
    public void getEyeshadowsConfig(FBConfigCallBack<List<Makeup>> callBack) {

        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                String res;
                try {
                    res = getFileString(PATH_EYESHADOW);
                    if (TextUtils.isEmpty(res)) {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(new ArrayList<>());
                            }
                        });
                    } else {
                        eyeshadowList = new Gson().fromJson(res, new TypeToken<EyeshadowConfig>() {}.getType());
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(eyeshadowList.getEyeshadows());
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
     * 获取缓存文件中眼线配置
     */
    public void getEyelinesConfig(FBConfigCallBack<List<Makeup>> callBack) {

        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                String res;
                try {
                    res = getFileString(PATH_EYELINE);
                    if (TextUtils.isEmpty(res)) {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(new ArrayList<>());
                            }
                        });
                    } else {
                        eyelineList = new Gson().fromJson(res, new TypeToken<EyelineConfig>() {}.getType());
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(eyelineList.getEyeliners());
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
     * 获取缓存文件中睫毛配置
     */
    public void getEyelashsConfig(FBConfigCallBack<List<Makeup>> callBack) {

        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                String res;
                try {
                    res = getFileString(PATH_EYELASH);
                    if (TextUtils.isEmpty(res)) {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(new ArrayList<>());
                            }
                        });
                    } else {
                        eyelashList = new Gson().fromJson(res, new TypeToken<EyelashConfig>() {}.getType());
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(eyelashList.getEyelashes());
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
     * 获取缓存文件中美瞳配置
     */
    public void getPupilsConfig(FBConfigCallBack<List<Makeup>> callBack) {

        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                String res;
                try {
                    res = getFileString(PATH_PUPILS);
                    if (TextUtils.isEmpty(res)) {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(new ArrayList<>());
                            }
                        });
                    } else {
                        pupilsList = new Gson().fromJson(res, new TypeToken<PupilsConfig>() {}.getType());
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(pupilsList.getPupils());
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
     * 获取缓存文件中轻美妆配置
     */
    public void getLightMakeupConfig(FBConfigCallBack<List<LightMakeupConfig.LightMakeup>> callBack) {
        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                try {
                    String res = getFileString(PATH_LIGHT_MAKEUP);
                    Log.i("gao", "getLigMakeupConfig:== "+res);
                    if (TextUtils.isEmpty(res)) {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(new ArrayList<>());
                            }
                        });
                    } else {
                        lightMakeupList = new Gson().fromJson(res, new TypeToken<LightMakeupConfig>() {}.getType());
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                callBack.success(lightMakeupList.getLight_makeup());
                            }
                        });
                    }

                } catch (Exception e) {
                    uiHandler.post(new Runnable() {
                        @Override public void run() {
                            Log.i("gao", "getLigMakeupConfig: =="+e.getMessage());
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
     * 更新轻美妆
     */
    public void ligMakeupDownload(String content) {
        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                modifyFile(content, PATH_LIGHT_MAKEUP);
            }
        });
    }
    /**
     * 更新gift文件
     *
     * @param content json 内容
     */
    public void giftDownload(final String content) {
        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                modifyFile(content, PATH_GIFT);
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
    /**
     * 更新美妆文件
     */
    public void makeupDownload(int type, String content) {
        cachedThreadPool.execute(new Runnable() {
            @Override public void run() {
                String res = "";
                switch (type){
                    case 0:
                        res = PATH_LIPSTICK;
                        break;
//                    case 1:
//                        res = PATH_EYEBROW;
//                        break;
//                    case 2:
//                        res = PATH_BLUSH;
//                        break;
                }
                modifyFile(content, res);
            }
        });
    }

}

