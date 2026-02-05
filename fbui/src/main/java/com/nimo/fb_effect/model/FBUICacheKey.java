package com.nimo.fb_effect.model;

/**
 * 缓存的key值
 */
public enum FBUICacheKey {

  BEAUTY_SKIN_SELECT_POSITION(0,""), //选中了哪个美肤
  BEAUTY_FACE_TRIM_SELECT_POSITION(0,""), //选中了哪个美型
  FILTER_SELECT_POSITION(1,""),//选中了哪个滤镜
//  FILTER_SELECT_POSITION(0,""),//选中了哪个滤镜

//  FILTER_SELECT_NAME(0,""),//选中了哪个滤镜
  FILTER_SELECT_NAME(0,"zhigan1"),//选中了哪个滤镜
  HAIR_SELECT_NAME(0,""),//选中了哪个美发
  HAIR_SELECT_POSITION(0,""),//选中了哪个美发
  EFFECT_FILTER_SELECT_POSITION,//选中了哪个趣味滤镜
  EFFECT_FILTER_SELECT_NAME,//选中了哪个趣味滤镜
  FUNNY_FILTER_SELECT_POSITION(0, ""),//选中了哪个趣味滤镜
  FUNNY_FILTER_SELECT_NAME,//选中了哪个趣味滤镜

    FACE_SHAPE_SELECT_POSITION(0,""), //选中了哪个脸型;
    LIGHT_MAKEUP_SELECT_POSITION(0,""), //选中了哪个轻彩妆;
    LIGHT_MAKEUP_SELECT_NAME(0,""),//选中了哪个轻彩妆
    BEAUTY_MAKEUP_SELECT_POSITION(0,""), //选中了哪个轻彩妆;
    BEAUTY_MAKEUP_SELECT_NAME(0,""),//选中了哪个轻彩妆

    LIPSTICK_SELECT_POSITION(0,""),//选中了哪个口红
    EYEBROW_SELECT_POSITION(0,""),//选中了哪个眉毛
    BLUSH_SELECT_POSITION(0,""),//选中了哪个腮红
    EYESHADOW_SELECT_POSITION,//选中了哪个眼影
    EYELINE_SELECT_POSITION,//选中了哪个眼线
    EYELASH_SELECT_POSITION,//选中了哪个睫毛
    PUPILS_SELECT_POSITION,//选中了哪个美瞳
    LIPSTICK_SELECT_COLOR_POSITION,//选中了哪个口红颜色位置
    EYEBROW_SELECT_COLOR_POSITION,//选中了哪个眉毛颜色位置
    BLUSH_SELECT_COLOR_POSITION,//选中了哪个腮红颜色位置
    LIPSTICK_SELECT_TYPE(0,"-1"),//选中了哪个口红
    EYEBROW_SELECT_TYPE(0,"-1"),//选中了哪个眉毛
    BLUSH_SELECT_TYPE(0,"-1"),//选中了哪个腮红
    EYESHADOW_SELECT_NAME,//选中了哪个眼影
    EYELINE_SELECT_NAME,//选中了哪个眼线
    EYELASH_SELECT_NAME,//选中了哪个睫毛
    PUPILS_SELECT_NAME,//选中了哪个美瞳
    LIPSTICK_COLOR_NAME(0,"rouhefen"), //选中了哪个口红的颜色名称
    EYEBROW_COLOR_NAME(0,"roufenzong"), //选中了哪个眉毛的颜色名称
    BLUSH_COLOR_NAME(0,"richang"), //选中了哪个腮红的颜色名称
    ;

  int defaultInt;
  String defaultStr;

  public int getDefaultInt() {
    return defaultInt;
  }

  public String getDefaultStr() {
    return defaultStr;
  }

  FBUICacheKey(int defaultInt, String defaultStr) {
    this.defaultInt = defaultInt;
    this.defaultStr = defaultStr;
  }

  FBUICacheKey() {
    defaultStr = "";
    defaultInt = 0;
  }

}
