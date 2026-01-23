package com.nimo.fb_effect.model;

import java.util.List;

/**
 * 美妆美瞳列表配置
 */

public class PupilsConfig {

  /**
   * stickers
   */
  private List<Makeup> makeup_pupils;

  @Override public String toString() {
    return "MakeupConfig{" +
        "htPupils=" + makeup_pupils.size() +
        "个}";

  }

  public List<Makeup> getPupils() {
    return makeup_pupils;

  }

  public PupilsConfig(List<Makeup> pupils) {
    this.makeup_pupils = pupils;
  }

  public void setPupils(List<Makeup> htPupils) {
    this.makeup_pupils = htPupils;
  }

}

