package com.nimo.fb_effect.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.nimo.fb_effect.R;

/**
 * 脸型枚举类
 */
public enum FBFaceShape {
    CLASSIC(R.string.face_classic, R.drawable.ic_ti_classic_unselected_full, R.drawable.ic_ti_classic, FBFaceShapeValue.CLASSIC_FACE_SHAPE),
    SQUARE_FACE(R.string.square_face, R.drawable.ic_ti_square_face_shape_unselected_full, R.drawable.square_face_shape, FBFaceShapeValue.SQUARE_FACE_SHAPE),
    LONG_FACE(R.string.long_face, R.drawable.ic_ti_long_face_shape_unselected_full, R.drawable.long_face_shape, FBFaceShapeValue.LONG_FACE_SHAPE),
//    longFace(R.string.long_face, R.drawable.ic_ti_long_face_shape_unselected_full, R.drawable.ic_ti_long_face_shape_selected, FBFaceShapeKey.LONG_FACE),
    ROUND_FACE(R.string.round_face, R.drawable.ic_ti_round_face_shape_unselected_full, R.drawable.round_face_shape, FBFaceShapeValue.ROUND_FACE_SHAPE),
    THIN_FACE(R.string.thin_face, R.drawable.ic_ti_thin_face_shape_unselected_full, R.drawable.thin_face_shape, FBFaceShapeValue.THIN_FACE_SHAPE);


    //名称
    private final int name;
    //黑色图标
    private final int drawableRes_black;
    //白色图标
    private final int drawableRes_white;
    //对应的key
    private final FBFaceShapeValue fbFaceShape;


    public FBFaceShapeValue getFBFaceShape() {
        return fbFaceShape;
    }

    public String getName(@NonNull Context context) {
        return context.getString(name);
    }

    public int getDrawableRes_black() {
        return drawableRes_black;
    }

    public int getDrawableRes_white() {
        return drawableRes_white;
    }

    FBFaceShape(@StringRes int name, @DrawableRes int drawableRes_black, @DrawableRes int drawableResWhite, FBFaceShapeValue fbFaceShape) {
        this.name = name;
        this.drawableRes_white = drawableResWhite;
        this.drawableRes_black = drawableRes_black;
        this.fbFaceShape = fbFaceShape;
    }
    public String getString(@NonNull Context context) {
        return context.getResources().getString(name);
    }
}
