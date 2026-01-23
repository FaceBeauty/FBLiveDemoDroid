package com.nimo.fb_effect.model;

import java.util.List;

/**
 * 美妆眼线列表配置
 */

public class EyelineConfig {

  /**
   * stickers
   */
  private List<Makeup> makeup_eyeline;

  @Override public String toString() {
    return "MakeupConfig{" +
        "htEyeliners=" + makeup_eyeline.size() +
        "个}";

  }

  public List<Makeup> getEyeliners() {
    return makeup_eyeline;

  }

  public EyelineConfig(List<Makeup> eyeliners) {
    this.makeup_eyeline = eyeliners;
  }

  public void setEyeliners(List<Makeup> htEyeliners) {
    this.makeup_eyeline = htEyeliners;
  }

}

