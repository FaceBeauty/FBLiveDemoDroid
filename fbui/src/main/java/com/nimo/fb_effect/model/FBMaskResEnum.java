package com.nimo.fb_effect.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.nimo.fb_effect.R;

public enum FBMaskResEnum {
    STICKER_CANCEL(R.drawable.icon_cancel),
    MASK_PURPLE(R.drawable.icon_mask_bluebird),
    MASK_BLUE(R.drawable.icon_mask_blue),
    MASK_GREEN(R.drawable.icon_mask_green),
    MASK_TIGER_HUANG(R.drawable.icon_mask_tiger_huang),
    MASK_TIGER_BAI(R.drawable.icon_mask_tiger_bai);



    FBMaskResEnum(int iconRes) {this.iconRes = iconRes;}

    //图标地址
    private final int iconRes;

    public Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, iconRes);
    }
}
