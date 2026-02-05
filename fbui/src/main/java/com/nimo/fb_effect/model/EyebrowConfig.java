package com.nimo.fb_effect.model;

import java.util.List;

/**
 * 美妆眉毛列表配置
 */

public class EyebrowConfig {

  /**
   * stickers
   */
  private List<Makeup> makeup_eyebrow;

  @Override public String toString() {
    return "MakeupConfig{" +
        "htEyebrows=" + makeup_eyebrow.size() +
        "个}";

  }

  public List<Makeup> getEyebrows() {
    return makeup_eyebrow;



  }

  public void setType(int type){

  }

  public EyebrowConfig(List<Makeup> eyebrows) {
    this.makeup_eyebrow = eyebrows;
  }

  public void setEyebrows(List<Makeup> htEyebrows) {
    this.makeup_eyebrow = htEyebrows;
  }

}

