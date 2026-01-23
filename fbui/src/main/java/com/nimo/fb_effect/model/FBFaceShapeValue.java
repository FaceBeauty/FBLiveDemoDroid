package com.nimo.fb_effect.model;



/**
 * 脸型及其对应的参数表
 */
public enum FBFaceShapeValue {
    CLASSIC_FACE_SHAPE(FBFaceShapeKey.CLASSIC, 0, 50, 0, 0, 0, 0, 10, 40, 0, 0, 50, 0, 0, 0, 0, 0, 0, 30, 0, 0),
    SQUARE_FACE_SHAPE(FBFaceShapeKey.SQUARE_FACE, 20, 40, 10, 0, 0, 0, 40, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30,0),
    LONG_FACE_SHAPE(FBFaceShapeKey.LONG_FACE, 0, 0, 0, 50, 0, 0, 0, 30, 0, 0, 30, 0, 0, 0, 0, 0, 20, 0, 0, 0),
    ROUND_FACE_SHAPE(FBFaceShapeKey.ROUND_FACE, 20, 40, 30, 0, 0, 0, 0, 20, 0, 20, 40, 0, 0, 10, 0, 0, 0, 0, 0, 0),
    THIN_FACE_SHAPE(FBFaceShapeKey.THIN_FACE, 0, 20, 0, 0, 0, 0, 0, 10, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    public FBFaceShapeKey faceShapeEnum;
    public int cheek_thinning;
    public int v_face;
    public int cheek_narrowing;
    public int short_face;
    public int face_lessening;
    public int cheek_bone_thinning;
    public int jaw_bone_thinning;
    public int eye_enlarging;
    public int circle_eye;
    public int chin_trimming;
    public int nose_thinning;

    public int mouth_trimming;
    public int eye_corner_enlarging;
    public int eye_sapcing;
    public int eye_corner_trimming;
    public int nose_enlarging;
    public int philtrum_trimming;
    public int mouth_smiling;
    public int temple_enlarging;
//    public int head_lessening;
    public int nose_root_enlarging;

    FBFaceShapeValue(FBFaceShapeKey faceShapeEnum, int cheek_thinning, int v_face, int cheek_narrowing, int short_face, int face_lessening, int cheek_bone_thinning, int jaw_bone_thinning, int eye_enlarging, int circle_eye, int chin_trimming, int nose_thinning, int mouth_trimming, int eye_corner_enlarging, int eye_sapcing, int eye_corner_trimming, int nose_enlarging, int philtrum_trimming, int mouth_smiling, int temple_enlarging,int nose_root_enlarging) {
        this.faceShapeEnum = faceShapeEnum;
        this.cheek_thinning = cheek_thinning;
        this.v_face = v_face;
        this.cheek_narrowing = cheek_narrowing;
        this.short_face = short_face;
        this.face_lessening = face_lessening;
        this.cheek_bone_thinning = cheek_bone_thinning;
        this.jaw_bone_thinning = jaw_bone_thinning;
        this.eye_enlarging = eye_enlarging;
        this.circle_eye = circle_eye;
        this.chin_trimming = chin_trimming;
        this.nose_thinning = nose_thinning;
        this.mouth_trimming = mouth_trimming;
        this.eye_corner_enlarging = eye_corner_enlarging;
        this.eye_sapcing = eye_sapcing;
        this.eye_corner_trimming = eye_corner_trimming;
        this.nose_enlarging = nose_enlarging;
        this.philtrum_trimming = philtrum_trimming;
        this.mouth_smiling = mouth_smiling;
        this.temple_enlarging = temple_enlarging;
//        this.head_lessening = head_lessening;
        this.nose_root_enlarging = nose_root_enlarging;
    }
    public static FBFaceShapeValue fromFaceShape(FBFaceShape faceShape) {
        for (FBFaceShapeValue value : values()) {
            if (value.faceShapeEnum.name().equals(faceShape.name())) {
                return value;
            }
        }
        return CLASSIC_FACE_SHAPE;
    }

}
