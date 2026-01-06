package com.nimo.fb_effect.utils.device;

// 芯片详细信息类
public class ChipInfo {
    private String CNName;
    private String ReleaseDate;
    private String Level;

    // 必须有默认构造方法（Gson反射需要）
    public ChipInfo() {}

    // getter方法
    public String getCNName() {
        return CNName;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }
    public String getLevel() {
        return Level;
    }
}

