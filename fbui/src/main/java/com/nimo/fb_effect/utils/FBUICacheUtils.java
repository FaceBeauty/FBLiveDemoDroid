package com.nimo.fb_effect.utils;

import android.content.Context;
import android.util.Log;
import com.nimo.fb_effect.model.FBBeautyKey;
import com.nimo.fb_effect.model.FBBeautyParam;
import com.nimo.fb_effect.model.FBFaceShape;
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
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyImageSharpness, beautySkinValue(FBBeautyKey.sharpening));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyImageClarity, beautySkinValue(FBBeautyKey.clearness));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyEyeBrightening, beautySkinValue(FBBeautyKey.eyeslight));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyToothWhitening, beautySkinValue(FBBeautyKey.teethwhite));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyFaceContouring, beautySkinValue(FBBeautyKey.sharpfeatured));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyDarkCircleLessening, beautySkinValue(FBBeautyKey.undereye_circles));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyNasolabialLessening, beautySkinValue(FBBeautyKey.nasolabial));
        FBEffect.shareInstance().setBeauty(FBBeautyParam.FBBeautyWhiteBalance, beautySkinValue(FBBeautyKey.whitebalance) - 50);
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
        //脸型-经典
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeEnlarging, 60);//大眼60
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekThinning, 60);//瘦脸60
        FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekNarrowing, 15);//窄脸15
        //轻美妆
//        FBEffect.shareInstance().setStyle(FBUICacheUtils.getLightMakeupName(),FBUICacheUtils.getL );//窄脸15
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
    //---------------脸型----------------------------------

    /**
     * 获取脸型位置
     * @return
     */
    public static int faceShapePosition() {
        return SharedPreferencesUtil.get(FBUICacheKey.FACE_SHAPE_SELECT_POSITION.name(),
                FBUICacheKey.FACE_SHAPE_SELECT_POSITION.getDefaultInt());
    }

    public static void faceShapePosition(int position) {
        SharedPreferencesUtil.put(FBUICacheKey.FACE_SHAPE_SELECT_POSITION.name(), position);
    }
    /**
     * 获取美妆位置
     * @return
     */
    public static int makeupPosition() {
        return SharedPreferencesUtil.get(FBUICacheKey.BEAUTY_MAKEUP_SELECT_NAME.name(),
                FBUICacheKey.BEAUTY_MAKEUP_SELECT_POSITION.getDefaultInt());
    }

    public static void makeupPosition(int position) {
        SharedPreferencesUtil.put(FBUICacheKey.BEAUTY_MAKEUP_SELECT_POSITION.name(), position);
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

    //--------------特效滤镜----------------------------------

    public static int getEffectFilterPosition() {

        return SharedPreferencesUtil.get(FBUICacheKey.EFFECT_FILTER_SELECT_POSITION.name(),
                FBUICacheKey.EFFECT_FILTER_SELECT_POSITION.getDefaultInt());
    }

    public static void setEffectFilterPosition(int position) {
        SharedPreferencesUtil.put(FBUICacheKey.EFFECT_FILTER_SELECT_POSITION.name(), position);
    }

    public static String getEffectFilterName() {

        return SharedPreferencesUtil.get(FBUICacheKey.EFFECT_FILTER_SELECT_NAME.name(),
                FBUICacheKey.EFFECT_FILTER_SELECT_NAME.getDefaultStr());
    }

    public static void setEffectFilterName(String name) {
        SharedPreferencesUtil.put(FBUICacheKey.EFFECT_FILTER_SELECT_NAME.name(), name);
    }

    //-------------------------------------------------
    //---------------趣味滤镜----------------------------------

    public static int getFunnyFilterPosition() {

        return SharedPreferencesUtil.get(FBUICacheKey.FUNNY_FILTER_SELECT_POSITION.name(),
                FBUICacheKey.FUNNY_FILTER_SELECT_POSITION.getDefaultInt());
    }

    public static void setFunnyFilterPosition(int position) {
        SharedPreferencesUtil.put(FBUICacheKey.FUNNY_FILTER_SELECT_POSITION.name(), position);
    }

    public static String getFunnyFilterName() {

        return SharedPreferencesUtil.get(FBUICacheKey.FUNNY_FILTER_SELECT_NAME.name(),
                FBUICacheKey.FUNNY_FILTER_SELECT_NAME.getDefaultStr());
    }

    public static void setFunnyFilterName(String name) {
        SharedPreferencesUtil.put(FBUICacheKey.FUNNY_FILTER_SELECT_NAME.name(), name);
    }
    //-------------------------------------------------

    //---------------美发----------------------------------

    public static int getBeautyHairPosition() {

        return SharedPreferencesUtil.get(FBUICacheKey.HAIR_SELECT_POSITION.name(),
                FBUICacheKey.HAIR_SELECT_POSITION.getDefaultInt());
    }

    public static void setBeautyHairPosition(int position) {
        SharedPreferencesUtil.put(FBUICacheKey.HAIR_SELECT_POSITION.name(), position);
    }

    public static String getBeautyHairName() {

        return SharedPreferencesUtil.get(FBUICacheKey.HAIR_SELECT_NAME.name(),
                FBUICacheKey.HAIR_SELECT_NAME.getDefaultStr());
    }

    public static void setBeautyHairName(String name) {
        SharedPreferencesUtil.put(FBUICacheKey.HAIR_SELECT_NAME.name(), name);
    }

    public static int setBeautyHairValue(String hairName) {
        return SharedPreferencesUtil.get("hair_" + hairName, 50);
    }

    public static void setBeautyHairValue(String hairName, int value) {
        SharedPreferencesUtil.put("hair_" + hairName, value);
    }
    //-----------------
    //---------------轻彩妆----------------------------------

    public static int getLightMakeupPosition() {

        return SharedPreferencesUtil.get(FBUICacheKey.LIGHT_MAKEUP_SELECT_POSITION.name(),
                FBUICacheKey.LIGHT_MAKEUP_SELECT_POSITION.getDefaultInt());
    }

    public static void setLightMakeupPosition(int position) {
        SharedPreferencesUtil.put(FBUICacheKey.LIGHT_MAKEUP_SELECT_POSITION.name(), position);
    }

    public static String getLightMakeupName() {

        return SharedPreferencesUtil.get(FBUICacheKey.LIGHT_MAKEUP_SELECT_NAME.name(),
                FBUICacheKey.LIGHT_MAKEUP_SELECT_NAME.getDefaultStr());
    }

    public static void setLightMakeupName(String name) {
        SharedPreferencesUtil.put(FBUICacheKey.LIGHT_MAKEUP_SELECT_NAME.name(), name);
    }

    public static int setLightMakeupValue(String lightMakeupName) {
        return SharedPreferencesUtil.get("lightMakeup_" + lightMakeupName, 50);
    }

    public static void setLightMakeupValue(String lightMakeupName, int value) {
        SharedPreferencesUtil.put("lightMakeup_" + lightMakeupName, value);
    }
    //---------------美妆----------------------------------

    public static int getBeautyMakeUpPosition() {

        return SharedPreferencesUtil.get(FBUICacheKey.BEAUTY_MAKEUP_SELECT_POSITION.name(),
                FBUICacheKey.BEAUTY_MAKEUP_SELECT_POSITION.getDefaultInt());
    }

    public static void setBeautyMakeupPosition(int position) {
        SharedPreferencesUtil.put(FBUICacheKey.BEAUTY_MAKEUP_SELECT_POSITION.name(), position);
    }

    public static String getBeautyMakeupName() {

        return SharedPreferencesUtil.get(FBUICacheKey.BEAUTY_MAKEUP_SELECT_NAME.name(),
                FBUICacheKey.BEAUTY_MAKEUP_SELECT_NAME.getDefaultStr());
    }

    public static void setBeautyMakeupName(String name) {
        SharedPreferencesUtil.put(FBUICacheKey.BEAUTY_MAKEUP_SELECT_NAME.name(), name);
    }

    public static int setBeautyMakeupValue(String beautyMakeupName) {
        return SharedPreferencesUtil.get("Makeup_" + beautyMakeupName, 50);
    }

    public static void setBeautyMakeupValue(String beautyMakeupName, int value) {
        SharedPreferencesUtil.put("Makeup_" + beautyMakeupName, value);
    }
    //-----------------

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
                defaultValue = 55;
                break;
            case rosiness:
                defaultValue = 30;
                break;
            case sharpening:
                defaultValue = 60;
                break;
            case clearness:
                defaultValue = 30;
                break;
            case sharpfeatured:
                defaultValue = 40;
                break;
            case eyeslight:
                defaultValue = 30;
                break;
            case teethwhite:
                break;
            case brightness:
                defaultValue = 50;
                break;
            case undereye_circles:
                defaultValue = 80;
                break;
            case nasolabial:
                defaultValue = 80;
                break;
            case whitebalance:
                defaultValue = 50;
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
    /**
     * 获取脸型默认参数
     *
     * @param key
     * @return int
     */
    public static int faceShapeValue(FBFaceShape key) {
        int defaultValue = 0;

        switch (key) {
            case CLASSIC:
                defaultValue = 50;
                break;
            case SQUARE_FACE:
                defaultValue = 50;
                break;
            case LONG_FACE:
                defaultValue = 50;
                break;
            case ROUND_FACE:
                defaultValue = 50;
                break;
            case THIN_FACE:
                defaultValue = 50;
                break;
        }

        return SharedPreferencesUtil.get("face_shape_" + key.name(),
                defaultValue);

    }

    public static void faceShapeValue(FBFaceShape key, int progress) {
        SharedPreferencesUtil
                .put("face_shape_" + key.name(),
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
                defaultValue = 0;
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
                break;
            case CHEEK_BONE_THINNING:
                break;
            case CHEEK_NARROWING:
                defaultValue = 0;
                break;
            case JAW_BONE_THINNING:
                defaultValue = 10;
                break;
            case CHEEK_SHORTENING:
                break;
            case EYE_ROUNDING:
                break;
            case CHEEK_V_SHAPING:
                defaultValue = 50;
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
                defaultValue = 50;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + key);
        }

        return SharedPreferencesUtil.get("beauty_face_trim_" + key.name(),
            defaultValue);

    }
    //---------------　美妆缓存　----------------------------------

    /**
     * 根据idCard获取美妆类型所处列表位置缓存
     *
     * @param idCard
     * @return 美妆类型所处列表位置
     */
    public static int getMakeupItemPositionCache(int idCard) {
        switch (idCard) {
            case 0:
                return SharedPreferencesUtil.get(FBUICacheKey.LIPSTICK_SELECT_POSITION.name(),
                        FBUICacheKey.LIPSTICK_SELECT_POSITION.getDefaultInt());
            case 1:
                return SharedPreferencesUtil.get(FBUICacheKey.EYEBROW_SELECT_POSITION.name(),
                        FBUICacheKey.EYEBROW_SELECT_POSITION.getDefaultInt());
            case 2:
                return SharedPreferencesUtil.get(FBUICacheKey.BLUSH_SELECT_POSITION.name(),
                        FBUICacheKey.BLUSH_SELECT_POSITION.getDefaultInt());
            case 3:
                return SharedPreferencesUtil.get(FBUICacheKey.EYESHADOW_SELECT_POSITION.name(),
                        FBUICacheKey.EYESHADOW_SELECT_POSITION.getDefaultInt());
            case 4:
                return SharedPreferencesUtil.get(FBUICacheKey.EYELINE_SELECT_POSITION.name(),
                        FBUICacheKey.EYELINE_SELECT_POSITION.getDefaultInt());
            case 5:
                return SharedPreferencesUtil.get(FBUICacheKey.EYELASH_SELECT_POSITION.name(),
                        FBUICacheKey.EYELASH_SELECT_POSITION.getDefaultInt());
            case 6:
                return SharedPreferencesUtil.get(FBUICacheKey.PUPILS_SELECT_POSITION.name(),
                        FBUICacheKey.PUPILS_SELECT_POSITION.getDefaultInt());
            default:
                return 0;
        }
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
    public static void faceShapeResetEnable(boolean enable) {
        SharedPreferencesUtil.put("faceShape_enable", enable);
    }

    public static boolean faceShapeResetEnable() {
        return SharedPreferencesUtil.get("faceShape_enable", false);
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
    public static void resetFaceShapeValue(Context context) {
        FBFaceShape[] items = FBFaceShape.values();
        for (FBFaceShape item : items) {
            SharedPreferencesUtil.remove(context, "face_shape_" + item.name());
        }
        faceShapePosition(0);
        initCache(false);
        FBState.setCurrentFaceShape(FBFaceShape.CLASSIC);
    }
    public static void beautyMakeUpResetEnable(boolean enable) {
        SharedPreferencesUtil.put("make_up_enable", enable);
    }

    public static boolean beautyMakeUpResetEnable() {
        return SharedPreferencesUtil.get("make_up_enable", false);
    }

    /**
     * 根据idCard获取美妆颜色位置缓存
     *
     * @param idCard
     * @return 美妆颜色位置
     */
    public static int getMakeupItemColorPositionCache(int idCard) {
        // 获取缓存中保存的选中位置
        switch (idCard) {
            case 0:
                return SharedPreferencesUtil.get(FBUICacheKey.LIPSTICK_SELECT_COLOR_POSITION.name(),
                        FBUICacheKey.LIPSTICK_SELECT_COLOR_POSITION.getDefaultInt());
            case 1:
                return SharedPreferencesUtil.get(FBUICacheKey.EYEBROW_SELECT_COLOR_POSITION.name(),
                        FBUICacheKey.EYEBROW_SELECT_COLOR_POSITION.getDefaultInt());
            case 2:
                return SharedPreferencesUtil.get(FBUICacheKey.BLUSH_SELECT_COLOR_POSITION.name(),
                        FBUICacheKey.BLUSH_SELECT_COLOR_POSITION.getDefaultInt());
            // 省略其他case...
            default:
                return -1;
        }
    }

    /**
     * 根据idCard获取美妆颜色缓存
     *
     * @param idCard
     * @return 美妆颜色
     */
    public static String getMakeupItemColorCache(int idCard) {
        switch (idCard) {
            case 0:
                return SharedPreferencesUtil.get(FBUICacheKey.LIPSTICK_COLOR_NAME.name(),
                        FBUICacheKey.LIPSTICK_COLOR_NAME.getDefaultStr());
            case 1:
                return SharedPreferencesUtil.get(FBUICacheKey.EYEBROW_COLOR_NAME.name(),
                        FBUICacheKey.EYEBROW_COLOR_NAME.getDefaultStr());
            case 2:
                return SharedPreferencesUtil.get(FBUICacheKey.BLUSH_COLOR_NAME.name(),
                        FBUICacheKey.BLUSH_COLOR_NAME.getDefaultStr());
            // 省略其他case...
            default:
                Log.e("makeup cache operation", "idCard is error while executing getMakeupItemColorCache");
                return "";
        }
    }
    /**
     * 设置美妆颜色位置缓存
     *
     * @param idCard
     * @param position
     */
    public static void setMakeupItemColorPositionCache(int idCard, int position) {
        // 保存选中的位置到缓存
        switch (idCard) {
            case 0:
                SharedPreferencesUtil.put(FBUICacheKey.LIPSTICK_SELECT_COLOR_POSITION.name(), position);
                break;
            case 1:
                SharedPreferencesUtil.put(FBUICacheKey.EYEBROW_SELECT_COLOR_POSITION.name(), position);
                break;
            case 2:
                SharedPreferencesUtil.put(FBUICacheKey.BLUSH_SELECT_COLOR_POSITION.name(), position);
                break;
            // 省略其他case...
            default:
                break;
        }
    }
    /**
     * 设置美妆颜色缓存
     *
     * @param idCard
     * @param name
     */
    public static void setMakeupItemColorCache(int idCard, String name) {
        switch (idCard) {
            case 0:
                SharedPreferencesUtil.put(FBUICacheKey.LIPSTICK_COLOR_NAME.name(), name);
                break;
            case 1:
                SharedPreferencesUtil.put(FBUICacheKey.EYEBROW_COLOR_NAME.name(), name);
                break;
            case 2:
                SharedPreferencesUtil.put(FBUICacheKey.BLUSH_COLOR_NAME.name(), name);
                break;
            // 省略其他case...
            default:
                Log.e("makeup cache operation", "idCard is error while executing setMakeupItemColorCache");
                break;
        }
    }
    /**
     * 设置美妆类型所处列表名称或类型缓存
     *
     * @param idCard
     * @param name_or_type
     */
    public static void setMakeupItemNameOrTypeCache(int idCard, String name_or_type) {
        switch (idCard) {
            case 0:
                SharedPreferencesUtil.put(FBUICacheKey.LIPSTICK_SELECT_TYPE.name(), name_or_type);
                break;
            case 1:
                SharedPreferencesUtil.put(FBUICacheKey.EYEBROW_SELECT_TYPE.name(), name_or_type);
                break;
            case 2:
                SharedPreferencesUtil.put(FBUICacheKey.BLUSH_SELECT_TYPE.name(), name_or_type);
                break;
            case 3:
                SharedPreferencesUtil.put(FBUICacheKey.EYESHADOW_SELECT_NAME.name(), name_or_type);
                break;
            case 4:
                SharedPreferencesUtil.put(FBUICacheKey.EYELINE_SELECT_NAME.name(), name_or_type);
                break;
            case 5:
                SharedPreferencesUtil.put(FBUICacheKey.EYELASH_SELECT_NAME.name(), name_or_type);
                break;
            case 6:
                SharedPreferencesUtil.put(FBUICacheKey.PUPILS_SELECT_NAME.name(), name_or_type);
                break;
        }
    }

    /**
     * 获取美妆滑动条参数缓存
     *
     * @param idCard
     * @param name_or_type
     * @return 滑动条参数
     */
    public static int getMakeupItemValueCache(int idCard, String name_or_type) {
        switch (idCard) {
            case 0:
                return SharedPreferencesUtil.get("makeup_lipstick_" + name_or_type, 70);
            case 1:
                return SharedPreferencesUtil.get("makeup_eyebrow_" + name_or_type, 70);
            case 2:
                return SharedPreferencesUtil.get("makeup_blush_" + name_or_type, 70);
            case 3:
                return SharedPreferencesUtil.get("makeup_eyeshadow_" + name_or_type, 70);
            case 4:
                return SharedPreferencesUtil.get("makeup_eyeline_" + name_or_type, 70);
            case 5:
                return SharedPreferencesUtil.get("makeup_eyelash_" + name_or_type, 70);
            case 6:
                return SharedPreferencesUtil.get("makeup_pupils_" + name_or_type, 70);
            default:
                return 0;
        }
    }

    /**
     * 设置美妆滑动条参数
     *
     * @param idCard
     * @param name_or_type
     * @param value
     */
    public static void setMakeupItemValueCache(int idCard, String name_or_type, int value) {
        switch (idCard) {
            case 0:
                SharedPreferencesUtil.put("makeup_lipstick_" + name_or_type, value);
                break;
            case 1:
                SharedPreferencesUtil.put("makeup_eyebrow_" + name_or_type, value);
                break;
            case 2:
                SharedPreferencesUtil.put("makeup_blush_" + name_or_type, value);
                break;
            case 3:
                SharedPreferencesUtil.put("makeup_eyeshadow_" + name_or_type, value);
                break;
            case 4:
                SharedPreferencesUtil.put("makeup_eyeline_" + name_or_type, value);
                break;
            case 5:
                SharedPreferencesUtil.put("makeup_eyelash_" + name_or_type, value);
                break;
            case 6:
                SharedPreferencesUtil.put("makeup_pupils_" + name_or_type, value);
                break;
        }
    }
    /**
     * 根据idCard获取美妆类型所处列表名称或类型缓存
     *
     * @param idCard
     * @return 美妆类型所处列表名称或类型
     */
    public static String getMakeupItemNameOrTypeCache(int idCard) {
        switch (idCard) {
            case 0:
                return SharedPreferencesUtil.get(FBUICacheKey.LIPSTICK_SELECT_TYPE.name(),
                        FBUICacheKey.LIPSTICK_SELECT_TYPE.getDefaultStr());
            case 1:
                return SharedPreferencesUtil.get(FBUICacheKey.EYEBROW_SELECT_TYPE.name(),
                        FBUICacheKey.EYEBROW_SELECT_TYPE.getDefaultStr());
            case 2:
                return SharedPreferencesUtil.get(FBUICacheKey.BLUSH_SELECT_TYPE.name(),
                        FBUICacheKey.BLUSH_SELECT_TYPE.getDefaultStr());
            case 3:
                return SharedPreferencesUtil.get(FBUICacheKey.EYESHADOW_SELECT_NAME.name(),
                        FBUICacheKey.EYESHADOW_SELECT_NAME.getDefaultStr());
            case 4:
                return SharedPreferencesUtil.get(FBUICacheKey.EYELINE_SELECT_NAME.name(),
                        FBUICacheKey.EYELINE_SELECT_NAME.getDefaultStr());
            case 5:
                return SharedPreferencesUtil.get(FBUICacheKey.EYELASH_SELECT_NAME.name(),
                        FBUICacheKey.EYELASH_SELECT_NAME.getDefaultStr());
            case 6:
                return SharedPreferencesUtil.get(FBUICacheKey.PUPILS_SELECT_NAME.name(),
                        FBUICacheKey.PUPILS_SELECT_NAME.getDefaultStr());
            default:
                return "";
        }
    }
    /**
     * 根据idCard设置美妆类型所处列表位置缓存
     *
     * @param idCard
     * @return 美妆类型所处列表位置
     */
    public static void setMakeupItemPostionCache(int idCard, int position) {
        switch (idCard) {
            case 0:
                SharedPreferencesUtil.put(FBUICacheKey.LIPSTICK_SELECT_POSITION.name(), position);
                break;
            case 1:
                SharedPreferencesUtil.put(FBUICacheKey.EYEBROW_SELECT_POSITION.name(), position);
                break;
            case 2:
                SharedPreferencesUtil.put(FBUICacheKey.BLUSH_SELECT_POSITION.name(), position);
                break;
            case 3:
                SharedPreferencesUtil.put(FBUICacheKey.EYESHADOW_SELECT_POSITION.name(), position);
                break;
            case 4:
                SharedPreferencesUtil.put(FBUICacheKey.EYELINE_SELECT_POSITION.name(), position);
                break;
            case 5:
                SharedPreferencesUtil.put(FBUICacheKey.EYELASH_SELECT_POSITION.name(), position);
                break;
            case 6:
                SharedPreferencesUtil.put(FBUICacheKey.PUPILS_SELECT_POSITION.name(), position);
                break;
        }
    }
}
