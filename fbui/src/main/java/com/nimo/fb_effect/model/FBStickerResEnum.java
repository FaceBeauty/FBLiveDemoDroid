package com.nimo.fb_effect.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.nimo.fb_effect.R;

public enum FBStickerResEnum {
    STICKER_CANCEL(R.drawable.icon_cancel),
    STICKER_LU(R.drawable.icon_sticker_lu),
    STICKER_MAO(R.drawable.icon_sticker_mao),
    STICKER_SHENGDAN(R.drawable.icon_sticker_shengdan),
    STICKER_XINCHUN(R.drawable.icon_sticker_xinchun),
    STICKER_TUZI(R.drawable.icon_sticker_tuzi),
    STICKER_XIAOZHU(R.drawable.icon_sticker_xiaozhu);



    FBStickerResEnum(int iconRes) {this.iconRes = iconRes;}

    //图标地址
    private final int iconRes;

    public Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, iconRes);
    }
}
