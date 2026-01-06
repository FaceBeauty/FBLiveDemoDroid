package com.nimo.fb_effect.model;

import android.util.Log;
import com.nimo.fb_effect.model.FBBeautyFilterConfig.FBBeautyFilter;
import com.nimo.fb_effect.utils.FBUICacheUtils;

/**
 * UI状态容器
 */
public class FBState {

  //一级面板是哪个
  public static FBViewState currentViewState = FBViewState.HIDE;

  //二级面板是哪个
  public static FBViewState currentSecondViewState = FBViewState.BEAUTY_SKIN;

  public static FBViewState currentSliderViewState = FBViewState.BEAUTY_SKIN;

  //是否处于第三面板
//  public static FBViewState currentThirdViewState = FBViewState.MAKEUP_OUT;

  // 当前选中了哪个美颜参数
  private static FBBeautyKey currentBeautySkin = FBBeautyKey.NONE;

  // 当前选中了哪个美型参数
  public static FBFaceTrim currentFaceTrim = FBFaceTrim.EYE_ENLARGING;


  // 当前选中了哪个滤镜
  public static FBBeautyFilter currentStyleFilter = FBBeautyFilter.NO_FILTER;
  public static FBEffectFilterConfig.FBEffectFilter currentEffectFilter = FBEffectFilterConfig.FBEffectFilter.NO_FILTER;
  // 当前选中了哪个美发
  public static FBHairConfig.FBHair currentHair = FBHairConfig.FBHair.NO_HAIR;
  public static FBFunnyFilterConfig.FBFunnyFilter currentFunnyFilter = FBFunnyFilterConfig.FBFunnyFilter.NO_FILTER;
  public static FBViewState currentAR = FBViewState.AR_PROP;
  //释放黑色主题
  public static boolean isDark = true;

  public static FBBeautyKey getCurrentBeautySkin() {
    return currentBeautySkin;
  }

  public static void setCurrentBeautySkin(FBBeautyKey currentBeautySkin) {
    Log.e("设置当前的美肤",currentBeautySkin.name());
    FBState.currentBeautySkin = currentBeautySkin;
  }

  //释放
  public static void release() {
    currentViewState = FBViewState.HIDE;
    currentSecondViewState = FBViewState.BEAUTY_SKIN;
    currentBeautySkin = FBBeautyKey.NONE;
    currentFaceTrim = FBFaceTrim.EYE_ENLARGING;

    FBUICacheUtils.beautyFaceTrimPosition(-1);
    FBUICacheUtils.beautySkinPosition(-1);
    //todo
//    FBUICacheUtils.setBeautyHairPosition(0);
  }
}
