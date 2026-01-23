package com.nimo.fb_effect.model;

import java.util.function.Function;

public enum FBFaceShapeReshapeMap {

    /* ============ 眼部 ============ */

    EYE_ENLARGING(
            FBBeautyParam.FBReshapeEyeEnlarging,
            FBFaceTrim.EYE_ENLARGING,
            v -> v.eye_enlarging,
            false
    ),

    EYE_ROUNDING(
            FBBeautyParam.FBReshapeEyeRounding,
            FBFaceTrim.EYE_ROUNDING,
            v -> v.circle_eye,
            false
    ),

    EYE_SAPCING(
            FBBeautyParam.FBReshapeEyeSpaceTrimming,
            FBFaceTrim.EYE_SAPCING,
            v -> v.eye_sapcing,
            true
    ),

    EYE_CORNER_TRIMMING(
            FBBeautyParam.FBReshapeEyeCornerTrimming,
            FBFaceTrim.EYE_CORNER_TRIMMING,
            v -> v.eye_corner_trimming,
            true
    ),

    EYE_CORNER_ENLARGING(
            FBBeautyParam.FBReshapeEyeCornerEnlarging,
            FBFaceTrim.EYE_CORNER_ENLARGING,
            v -> v.eye_corner_enlarging,
            false
    ),

    /* ============ 脸型 ============ */

    CHEEK_THINNING(
            FBBeautyParam.FBReshapeCheekThinning,
            FBFaceTrim.CHEEK_THINNING,
            v -> v.cheek_thinning,
            false
    ),

    CHEEK_V_SHAPING(
            FBBeautyParam.FBReshapeCheekVShaping,
            FBFaceTrim.CHEEK_V_SHAPING,
            v -> v.v_face,
            false
    ),

    CHEEK_NARROWING(
            FBBeautyParam.FBReshapeCheekNarrowing,
            FBFaceTrim.CHEEK_NARROWING,
            v -> v.cheek_narrowing,
            false
    ),

    CHEEK_SHORTENING(
            FBBeautyParam.FBReshapeCheekShortening,
            FBFaceTrim.CHEEK_SHORTENING,
            v -> v.short_face,
            false
    ),

    FACE_LESSENING(
            FBBeautyParam.FBReshapeFaceLessening,
            FBFaceTrim.FACE_LESSENING,
            v -> v.face_lessening,
            false
    ),

    CHEEK_BONE_THINNING(
            FBBeautyParam.FBReshapeCheekboneThinning,
            FBFaceTrim.CHEEK_BONE_THINNING,
            v -> v.cheek_bone_thinning,
            false
    ),

    JAW_BONE_THINNING(
            FBBeautyParam.FBReshapeJawboneThinning,
            FBFaceTrim.JAW_BONE_THINNING,
            v -> v.jaw_bone_thinning,
            false
    ),

    /* ============ 鼻 / 嘴 / 下巴 ============ */

    NOSE_THINNING(
            FBBeautyParam.FBReshapeNoseThinning,
            FBFaceTrim.NOSE_THINNING,
            v -> v.nose_thinning,
            false
    ),

    NOSE_ENLARGING(
            FBBeautyParam.FBReshapeNoseEnlarging,
            FBFaceTrim.NOSE_ENLARGING,
            v -> v.nose_enlarging,
            true
    ),

    NOSE_ROOT_ENLARGING(
            FBBeautyParam.FBReshapeNoseRootEnlarging,
            FBFaceTrim.NOSE_ROOT_ENLARGING,
            v -> v.nose_root_enlarging,
            false
    ),

    MOUTH_TRIMMING(
            FBBeautyParam.FBReshapeMouthTrimming,
            FBFaceTrim.MOUTH_TRIMMING,
            v -> v.mouth_trimming,
            true
    ),

    MOUTH_SMILING(
            FBBeautyParam.FBReshapeMouthSmiling,
            FBFaceTrim.MOUTH_SMILING,
            v -> v.mouth_smiling,
            false
    ),

    CHIN_TRIMMING(
            FBBeautyParam.FBReshapeChinTrimming,
            FBFaceTrim.CHIN_TRIMMING,
            v -> v.chin_trimming,
            true
    ),

    PHILTRUM_TRIMMING(
            FBBeautyParam.FBReshapePhiltrumTrimming,
            FBFaceTrim.PHILTRUM_TRIMMING,
            v -> v.philtrum_trimming,
            true
    ),

    TEMPLE_ENLARGING(
            FBBeautyParam.FBReshapeTempleEnlarging,
            FBFaceTrim.TEMPLE_ENLARGING,
            v -> v.temple_enlarging,
            false
    );

    /* ============ 公共字段 ============ */

    public final int reshapeParam;
    public final FBFaceTrim faceTrim;
    public final Function<FBFaceShapeValue, Integer> extractor;
    public final boolean uiNeedOffset50;

    FBFaceShapeReshapeMap(
            int reshapeParam,
            FBFaceTrim faceTrim,
            Function<FBFaceShapeValue, Integer> extractor,
            boolean uiNeedOffset50
    ) {
        this.reshapeParam = reshapeParam;
        this.faceTrim = faceTrim;
        this.extractor = extractor;
        this.uiNeedOffset50 = uiNeedOffset50;
    }
}
