package com.nimo.fb_effect.model;

import java.util.List;

/**
 * 美妆口红列表配置
 */

public class LipstickConfig {

  /**
   * stickers
   */
  private List<Makeup> makeup_lipstick;



  @Override public String toString() {
    return "HtMakeupConfig{" +
        "htMakeups=" + makeup_lipstick.size() +
        "个}";

  }

  public List<Makeup> getLipsticks() {
    return makeup_lipstick;
  }

  public LipstickConfig(List<Makeup> lipsticks) {
    this.makeup_lipstick = lipsticks;
  }

  public void setLipsticks(List<Makeup> lipsticks) {
    this.makeup_lipstick = lipsticks;
  }





  }


