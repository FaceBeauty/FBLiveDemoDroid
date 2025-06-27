package com.nimo.fb_effect.model;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.nimo.fb_effect.R;

/**
 * 美肤枚举类
 */
public enum FBBeauty {
  whiteness(R.string.whiteness, R.drawable.ic_whiteness_black, R.drawable.ic_whiteness_white, FBBeautyKey.whiteness),
  blurriness(R.string.blurriness, R.drawable.ic_blurriness_black, R.drawable.ic_blurriness_white, FBBeautyKey.blurriness),
  rosiness(R.string.rosiness, R.drawable.ic_rosiness_black, R.drawable.ic_rosiness_white, FBBeautyKey.rosiness),
  clearness(R.string.clearness, R.drawable.ic_clearness_black, R.drawable.ic_clearness_white, FBBeautyKey.clearness),
  sharpening(R.string.sharpening, R.drawable.ic_sharpness_black, R.drawable.ic_sharpness_white, FBBeautyKey.sharpening),
  sharpfeatured(R.string.sharpfeatured, R.drawable.ic_sharpfeatured_black, R.drawable.ic_sharpfeatured_white, FBBeautyKey.sharpfeatured),
  brightness(R.string.brightness, R.drawable.ic_brightness_black, R.drawable.ic_brightness_white, FBBeautyKey.brightness),
  undereye_circles(R.string.undereye_circles, R.drawable.ic_dark_circle_black, R.drawable.ic_dark_circle_white, FBBeautyKey.undereye_circles),
  nasolabial(R.string.nasolabial_fold, R.drawable.ic_nasolabial_black, R.drawable.ic_nasolabial_white, FBBeautyKey.nasolabial),
  whitebalance(R.string.withe_balance, R.drawable.ic_whitebalance_black, R.drawable.ic_whitebalance_white, FBBeautyKey.whitebalance),
  eyeslight(R.string.eyes_light, R.drawable.ic_eyelight_black, R.drawable.ic_eyelight_white, FBBeautyKey.eyeslight),
  teethwhite(R.string.teeth_white, R.drawable.ic_teethwhite_black, R.drawable.ic_teethwhite_white, FBBeautyKey.teethwhite),
  tracker1(R.string.tracker1, R.drawable.ic_nasolabial_black, R.drawable.ic_nasolabial_white, FBBeautyKey.tracker1),
  tracker2(R.string.tracker2, R.drawable.ic_nasolabial_black, R.drawable.ic_nasolabial_white, FBBeautyKey.tracker2),
  tracker3(R.string.tracker3, R.drawable.ic_nasolabial_black, R.drawable.ic_nasolabial_white, FBBeautyKey.tracker3);

  //名称
  private final int name;
  //黑色图标
  private final int drawableRes_black;
  //白色图标
  private final int drawableRes_white;
  //对应的key
  private final FBBeautyKey fbBeautyKey;


  public FBBeautyKey getFBBeautyKey() {
    return fbBeautyKey;
  }

  public String getName(@NonNull Context context) {
    return context.getString(name);
  }

  public int getDrawableRes_black() {
    return drawableRes_black;
  }

  public int getDrawableRes_white() {
    return drawableRes_white;
  }

  FBBeauty(@StringRes int name, @DrawableRes int drawableRes_black, @DrawableRes int drawableResWhite, FBBeautyKey fbBeautyKey) {
    this.name = name;
    this.drawableRes_white = drawableResWhite;
    this.drawableRes_black = drawableRes_black;
    this.fbBeautyKey = fbBeautyKey;
  }
}
