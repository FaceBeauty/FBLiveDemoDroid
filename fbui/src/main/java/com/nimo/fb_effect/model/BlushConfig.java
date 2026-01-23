package com.nimo.fb_effect.model;

import java.util.List;

/**
 * 美妆腮红列表配置
 */

public class BlushConfig {

  /**
   * stickers
   */
  private List<Makeup> makeup_blush;

  @Override public String toString() {
    return "MakeupConfig{" +
        "htBlushes=" + makeup_blush.size() +
        "个}";

  }

  public List<Makeup> getBlushes() {
    return makeup_blush;

  }

  public BlushConfig(List<Makeup> blushes) {
    this.makeup_blush = blushes;
  }

  public void setBlushes(List<Makeup> htBlushes) {
    this.makeup_blush = htBlushes;
  }

}

