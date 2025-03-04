package com.nimo.fb_effect.model;

/**
 * 缓存的key值
 */
public enum FBUICacheKey {

  BEAUTY_SKIN_SELECT_POSITION(0,""), //选中了哪个美肤
  BEAUTY_FACE_TRIM_SELECT_POSITION(0,""), //选中了哪个美型
  FILTER_SELECT_POSITION(2,""),//选中了哪个滤镜
  FILTER_SELECT_NAME(0,"ziran2"),//选中了哪个滤镜
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
