package com.nimo.fb_effect.utils.device;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class ChipInfoManager {
    // 单例实例（volatile保证多线程可见性）
    private static volatile ChipInfoManager instance;
    private final Context context;
    private BrandChips brandChips;

    private ChipInfoManager(Context context) {
        this.context = context.getApplicationContext();
        parseJsonFromAssets();
    }

    // 全局获取单例的方法
    public static ChipInfoManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ChipInfoManager.class) {
                if (instance == null) {
                    instance = new ChipInfoManager(context);
                }
            }
        }
        return instance;
    }

    // 从assets读取并解析JSON
    private void parseJsonFromAssets() {
        try {
            InputStream is = context.getAssets().open("DeviceType.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            is.close();

            Gson gson = new Gson();
            brandChips = gson.fromJson(sb.toString(), BrandChips.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取高通芯片中文名称
    public String getQualcommChipName(String chipModel) {
        if (brandChips == null || brandChips.getQualcomm() == null) {
            return null;
        }
        ChipInfo info = brandChips.getQualcomm().get(chipModel);
        return info != null ? info.getCNName() : null;
    }
    // 获取高通芯片发布日期
    public String getQualcommReleaseDate(String chipModel) {
        if (brandChips == null || brandChips.getQualcomm()== null) {
            return null;
        }
        ChipInfo info = brandChips.getQualcomm().get(chipModel);
        return info != null ? info.getReleaseDate() : null;
    }
    // 获取某个高通芯片是哪个系列
    public String getQualcommChipsLevel(String chipModel) {
        if (brandChips == null || brandChips.getQualcomm()== null) {
            return null;
        }
        ChipInfo info = brandChips.getQualcomm().get(chipModel);
        return info != null ? info.getLevel() : null;
    }
    // 获取联发科芯片中文名称
    public String getMediaTekChipName(String chipModel) {
        if (brandChips == null || brandChips.getMediaTek() == null) {
            return null;
        }
        ChipInfo info = brandChips.getMediaTek().get(chipModel);
        return info != null ? info.getCNName() : null;
    }
    // 获取联发科芯片发布日期
    public String getMediaTekReleaseDate(String chipModel) {
        if (brandChips == null || brandChips.getMediaTek() == null) {
            return null;
        }
        ChipInfo info = brandChips.getMediaTek().get(chipModel);
        return info != null ? info.getReleaseDate() : null;
    }
    // 获取某个联发科芯片是哪个系列
    public String getMediaTekChipsLevel(String chipModel) {
        if (brandChips == null || brandChips.getMediaTek()== null) {
            return null;
        }
        ChipInfo info = brandChips.getMediaTek().get(chipModel);
        return info != null ? info.getLevel() : null;
    }

    // 获取三星所有芯片型号
    public String[] getSamsungChipModels() {
        if (brandChips == null || brandChips.getSamsung() == null) {
            return new String[0];
        }
        Map<String, ChipInfo> samsungMap = brandChips.getSamsung();
        return samsungMap.keySet().toArray(new String[0]);
    }
}