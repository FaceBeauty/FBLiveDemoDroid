package com.nimo.fb_effect.utils.device;

import java.util.Map;

// 品牌对应的芯片集合类
public class BrandChips {
    private Map<String, ChipInfo> Qualcomm;
    private Map<String, ChipInfo> MediaTek;
    private Map<String, ChipInfo> Samsung;

    // 必须有默认构造方法
    public BrandChips() {}

    // getter方法
    public Map<String, ChipInfo> getQualcomm() {
        return Qualcomm;
    }

    public Map<String, ChipInfo> getMediaTek() {
        return MediaTek;
    }

    public Map<String, ChipInfo> getSamsung() {
        return Samsung;
    }
}
