package com.nimo.fb_effect.utils;

import android.content.Context;
import android.util.Log;
import com.nimo.fb_effect.model.FBBeautyKey;
import com.nimo.fb_effect.model.FBBeautyParam;
import com.nimo.fb_effect.model.FBFaceTrim;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.FBUICacheKey;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBFilterEnum;

/**
 * ui缓存工具类
 */
public class FBUICacheUtils {

    /**
     * 载入缓存参数
     */
    public static void initCache(boolean isFirstInit) {

        FBEffect.shareInstance().setRenderEnable(true);

        if (isFirstInit) {
            FBState.release();

        }

        //设置滤镜
        FBEffect.shareInstance().setFilter(FBFilterEnum.FBFilterBeauty.getValue(), getBeautyFilterName(),getBeautyFilterValue(getBeautyFilterName()));

        //美肤系
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyClearSmoothing, beautySkinValue(FBBeautyKey.blurriness));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautySkinRosiness, beautySkinValue(FBBeautyKey.rosiness));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautySkinWhitening, beautySkinValue(FBBeautyKey.whiteness));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyImageBrightness, beautySkinValue(FBBeautyKey.brightness) - 50);
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyImageSharpness, beautySkinValue(FBBeautyKey.clearness));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyDarkCircleLessening, beautySkinValue(FBBeautyKey.undereye_circles));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyNasolabialLessening, beautySkinValue(FBBeautyKey.nasolabial));
        //美型系
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeEnlarging, beautyFaceTrimValue(FBFaceTrim.EYE_ENLARGING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeRounding, beautyFaceTrimValue(FBFaceTrim.EYE_ROUNDING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekThinning, beautyFaceTrimValue(FBFaceTrim.CHEEK_THINNING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekVShaping, beautyFaceTrimValue(FBFaceTrim.CHEEK_V_SHAPING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekNarrowing, beautyFaceTrimValue(FBFaceTrim.CHEEK_NARROWING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekboneThinning, beautyFaceTrimValue(FBFaceTrim.CHEEK_BONE_THINNING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeJawboneThinning, beautyFaceTrimValue(FBFaceTrim.JAW_BONE_THINNING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeChinTrimming, beautyFaceTrimValue(FBFaceTrim.CHIN_TRIMMING) - 50);
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeTempleEnlarging, beautyFaceTrimValue(FBFaceTrim.TEMPLE_ENLARGING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeHeadLessening, beautyFaceTrimValue(FBFaceTrim.head_lessening));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeFaceLessening, beautyFaceTrimValue(FBFaceTrim.FACE_LESSENING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeForeheadTrimming, beautyFaceTrimValue(FBFaceTrim.FOREHEAD_TRIM) - 50);
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeNoseThinning, beautyFaceTrimValue(FBFaceTrim.NOSE_THINNING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeCornerEnlarging, beautyFaceTrimValue(FBFaceTrim.EYE_CORNER_ENLARGING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeSpaceTrimming, beautyFaceTrimValue(FBFaceTrim.EYE_SAPCING) - 50);
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeCornerTrimming, beautyFaceTrimValue(FBFaceTrim.EYE_CORNER_TRIMMING) - 50);
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeNoseEnlarging, beautyFaceTrimValue(FBFaceTrim.NOSE_ENLARGING) - 50);
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapePhiltrumTrimming, beautyFaceTrimValue(FBFaceTrim.PHILTRUM_TRIMMING) - 50);
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeNoseApexLessening, beautyFaceTrimValue(FBFaceTrim.NOSE_APEX_LESSENING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeNoseRootEnlarging, beautyFaceTrimValue(FBFaceTrim.NOSE_ROOT_ENLARGING));
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeMouthTrimming, beautyFaceTrimValue(FBFaceTrim.MOUTH_TRIMMING) - 50);
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeMouthSmiling, beautyFaceTrimValue(FBFaceTrim.MOUTH_SMILING));

    }

    //---------美肤选中了哪个-------------------
    public static int beautySkinPosition() {
        return SharedPreferencesUtil
                .get(FBUICacheKey.BEAUTY_SKIN_SELECT_POSITION.name(),
                        FBUICacheKey.BEAUTY_SKIN_SELECT_POSITION.getDefaultInt());
    }

    public static void beautySkinPosition(int position) {
        Log.e("beautySkinPosition", position + "");
        SharedPreferencesUtil.put(FBUICacheKey.BEAUTY_SKIN_SELECT_POSITION.name(),
                position);
    }

    //-------------------------------------------------

    //---------------美型----------------------------------
    public static int beautyFaceTrimPosition() {
        return SharedPreferencesUtil
                .get(FBUICacheKey.BEAUTY_FACE_TRIM_SELECT_POSITION.name(),
                        FBUICacheKey.BEAUTY_FACE_TRIM_SELECT_POSITION.getDefaultInt());
    }

    public static void beautyFaceTrimPosition(int position) {
        SharedPreferencesUtil.put(FBUICacheKey.BEAUTY_FACE_TRIM_SELECT_POSITION.name(),
                position);
    }

    //-------------------------------------------------



    //---------------风格滤镜----------------------------------

    /**
     * 获取风格滤镜位置
     * @return
     */
    public static int getBeautyFilterPosition() {

        return SharedPreferencesUtil.get(FBUICacheKey.FILTER_SELECT_POSITION.name(),
                FBUICacheKey.FILTER_SELECT_POSITION.getDefaultInt());
    }

    public static void setBeautyFilterPosition(int position) {
        SharedPreferencesUtil.put(FBUICacheKey.FILTER_SELECT_POSITION.name(), position);
    }

    /**
     * 获取风格滤镜名称
     * @return
     */
    public static String getBeautyFilterName() {

        return SharedPreferencesUtil.get(FBUICacheKey.FILTER_SELECT_NAME.name(),
                FBUICacheKey.FILTER_SELECT_NAME.getDefaultStr());
    }

    public static void setBeautyFilterName(String name) {
        SharedPreferencesUtil.put(FBUICacheKey.FILTER_SELECT_NAME.name(), name);
    }

    public static int getBeautyFilterValue(String filterName) {
        return SharedPreferencesUtil.get("filter_" + filterName, 40);
    }

    public static void setBeautyFilterValue(String filterName, int value) {
        SharedPreferencesUtil.put("filter_" + filterName, value);
    }

    //-------------------------------------------------



    public static int previewInitialWidth() {

        return SharedPreferencesUtil.get("previewInitialWidth",
                0);
    }

    public static void previewInitialWidth(int width) {
        SharedPreferencesUtil.put("previewInitialWidth", width);
    }

    public static int previewInitialHeight() {

        return SharedPreferencesUtil.get("previewInitialHeight",
                0);
    }

    public static void previewInitialHeight(int height) {
        SharedPreferencesUtil.put("previewInitialHeight", height);
    }

    /**
     * 获取美肤默认参数
     *
     * @param key
     * @return int
     */
    public static int beautySkinValue(FBBeautyKey key) {

        int defaultValue = 0;

        switch (key) {
            case whiteness:
                defaultValue = 40;
                break;
            case blurriness:
                defaultValue = 60;
                break;
            case rosiness:
                defaultValue = 10;
                break;
            case clearness:
                defaultValue = 5;
                break;
            case brightness:
                defaultValue = 55;
                break;
            case undereye_circles:
                defaultValue = 10;
                break;
            case nasolabial:
                defaultValue = 10;
                break;
            case NONE:
                break;
        }

        return SharedPreferencesUtil.get("beauty_skin_" + key.name(),
                defaultValue);

    }

    public static void beautySkinValue(FBBeautyKey key, int progress) {
        SharedPreferencesUtil
                .put("beauty_skin_" + key.name(),
                        progress);
    }
    //-------------------------------------------------

    //---------------美型子功能参数----------------------------------
    public static int beautyFaceTrimValue(FBFaceTrim key) {

        int defaultValue = 0;

        switch (key) {
            case EYE_ENLARGING:
                defaultValue = 40;
                break;
            case EYE_CORNER_ENLARGING:
                break;
            case CHEEK_THINNING:
                defaultValue = 10;
                break;
            case NOSE_APEX_LESSENING:
                break;
            case NOSE_ROOT_ENLARGING:
                break;
            case MOUTH_SMILING:
                defaultValue = 30;
                break;
            case FACE_LESSENING:
                break;
            case TEMPLE_ENLARGING:
                defaultValue = 50;
                break;
            case CHEEK_BONE_THINNING:
                break;
            case CHEEK_NARROWING:
                break;
            case JAW_BONE_THINNING:
                defaultValue = 10;
                break;
            case CHEEK_SHORTENING:
                break;
            case EYE_ROUNDING:
                break;
            case CHEEK_V_SHAPING:
                defaultValue = 40;
                break;
            case CHIN_TRIMMING:
                defaultValue = 50;
                break;
            case PHILTRUM_TRIMMING:
                defaultValue = 50;
                break;
            case FOREHEAD_TRIM:
                defaultValue = 50;
                break;
            case EYE_SAPCING:
                defaultValue = 50;
                break;
            case EYE_CORNER_TRIMMING:
                defaultValue = 50;
                break;
            case NOSE_ENLARGING:
                defaultValue = 50;
                break;
            case NOSE_THINNING:
                defaultValue = 50;
                break;
            case MOUTH_TRIMMING:
                defaultValue = 50;
                break;
            case head_lessening:
                defaultValue = 0;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + key);
        }

        return SharedPreferencesUtil.get("beauty_face_trim_" + key.name(),
                defaultValue);

    }

    public static void beautyFaceTrimValue(FBFaceTrim key, int progress) {
        SharedPreferencesUtil
                .put("beauty_face_trim_" + key.name(),
                        progress);
    }


    //----------------------是否可用重置---------------------------

    public static void beautySkinResetEnable(boolean enable) {
        SharedPreferencesUtil.put("skin_enable", enable);
    }

    public static boolean beautySkinResetEnable() {
        return SharedPreferencesUtil.get("skin_enable", false);
    }

    public static void beautyFaceTrimResetEnable(boolean enable) {
        SharedPreferencesUtil.put("face_trim_enable", enable);
    }

    public static boolean beautyFaceTrimResetEnable() {
        return SharedPreferencesUtil.get("face_trim_enable", false);
    }


    //------------------重置相关---------------------------

    public static void resetFaceTrimValue(Context context) {
        FBFaceTrim[] items = FBFaceTrim.values();
        for (FBFaceTrim item : items) {
            SharedPreferencesUtil.remove(context, "beauty_face_trim_" + item.name());
        }
        beautyFaceTrimPosition(-1);
        initCache(false);
        FBState.currentFaceTrim = FBFaceTrim.EYE_ENLARGING;
    }

    public static void resetSkinValue(Context context) {
        FBBeautyKey[] items = FBBeautyKey.values();
        for (FBBeautyKey item : items) {
            SharedPreferencesUtil.remove(context, "beauty_skin_" + item.name());
        }
        beautySkinPosition(-1);
        initCache(false);
        FBState.setCurrentBeautySkin(FBBeautyKey.NONE);
    }


}
