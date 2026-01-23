package com.nimo.fb_effect.model;

import java.util.List;

/**
 * 美妆睫毛列表配置
 */

public class EyelashConfig {

  /**
   * stickers
   */
  private List<Makeup> makeup_eyelash;

  @Override public String toString() {
    return "MakeupConfig{" +
        "htEyelashes=" + makeup_eyelash.size() +
        "个}";

  }

  public List<Makeup> getEyelashes() {
    return makeup_eyelash;

  }

  public EyelashConfig(List<Makeup> eyelashes) {
    this.makeup_eyelash = eyelashes;
  }

  public void setEyelashs(List<Makeup> htEyelashes) {
    this.makeup_eyelash = htEyelashes;
  }

}

