package com.nimo.fb_effect.model;

import java.util.List;

/**
 * 美妆眼影列表配置
 */

public class EyeshadowConfig {

  /**
   * stickers
   */
  private List<Makeup> makeup_eyeshadow;

  @Override public String toString() {
    return "MakeupConfig{" +
        "htEyeshadows=" + makeup_eyeshadow.size() +
        "个}";

  }

  public List<Makeup> getEyeshadows() {
    return makeup_eyeshadow;

  }

  public EyeshadowConfig(List<Makeup> eyeshadows) {
    this.makeup_eyeshadow = eyeshadows;
  }

  public void setEyeshadows(List<Makeup> htEyeshadows) {
    this.makeup_eyeshadow = htEyeshadows;
  }

}

