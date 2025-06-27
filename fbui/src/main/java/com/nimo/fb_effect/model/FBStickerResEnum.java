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
    STICKER_XIAOZHU(R.drawable.icon_sticker_xiaozhu),
    STICKER_KAOLA(R.drawable.icon_sticker_kaola),
    STICKER_LAOSHU(R.drawable.icon_sticker_laoshu),
    STICKER_XIAOGOU(R.drawable.icon_sticker_xiaogou),
    STICKER_XIAOLU(R.drawable.icon_sticker_xiaolu),
    STICKER_XIAOMAO(R.drawable.icon_sticker_xiaomao);
    //新增
//    STICKER_APPLE(R.drawable.icon_sticker_xiaomao),
//    STICKER_CHUSHIMAO(R.drawable.icon_sticker_xiaomao),
//    STICKER_HUAHUAN(R.drawable.icon_sticker_xiaomao),
//    STICKER_HUDIEJIE(R.drawable.icon_sticker_xiaomao),
//    STICKER_HULUOBO(R.drawable.icon_sticker_xiaomao),
//    STICKER_HUZI(R.drawable.icon_sticker_xiaomao),
//    STICKER_JIAFEI(R.drawable.icon_sticker_xiaomao),
//    STICKER_MAIMAI(R.drawable.icon_sticker_xiaomao),
//    STICKER_SHENGRIMAO(R.drawable.icon_sticker_xiaomao),
//    STICKER_STAR(R.drawable.icon_sticker_xiaomao),
//    STICKER_TIANSHI(R.drawable.icon_sticker_xiaomao),
//    STICKER_YAYA(R.drawable.icon_sticker_xiaomao),
//    STICKER_YEAH(R.drawable.icon_sticker_xiaomao),
//    STICKER_YUSAN(R.drawable.icon_sticker_xiaomao),
//    STICKER_APPLETREE(R.drawable.icon_sticker_xiaomao),
//    STICKER_BUDING(R.drawable.icon_sticker_xiaomao),
//    STICKER_CHEER(R.drawable.icon_sticker_xiaomao),
//    STICKER_DAXIANG(R.drawable.icon_sticker_xiaomao),
//    STICKER_DOGDOG(R.drawable.icon_sticker_xiaomao),
//    STICKER_ERJI(R.drawable.icon_sticker_xiaomao),
//    STICKER_FDJ(R.drawable.icon_sticker_xiaomao),
//    STICKER_HUANGGUAN(R.drawable.icon_sticker_xiaomao),
//    STICKER_HUDIE(R.drawable.icon_sticker_xiaomao),
//    STICKER_KT(R.drawable.icon_sticker_xiaomao),
//    STICKER_MALIAO(R.drawable.icon_sticker_xiaomao),
//    STICKER_RED(R.drawable.icon_sticker_xiaomao),
//    STICKER_TU(R.drawable.icon_sticker_xiaomao),
//    STICKER_TUZIFAGU(R.drawable.icon_sticker_xiaomao),
//    STICKER_XIAOXIONG(R.drawable.icon_sticker_xiaomao),
//    STICKER_XIAOXIONGMAO(R.drawable.icon_sticker_xiaomao),
//    STICKER_XIAOYANG(R.drawable.icon_sticker_xiaomao),
//    STICKER_ZHU(R.drawable.icon_sticker_xiaomao),
//    STICKER_HUAHUAN1(R.drawable.icon_sticker_xiaomao),
//    STICKER_LAOSHU1(R.drawable.icon_sticker_xiaomao),
//    STICKER_YEAH1(R.drawable.icon_sticker_xiaomao),
//    STICKER_JIXIE(R.drawable.icon_sticker_xiaomao),
//    STICKER_LAN(R.drawable.icon_sticker_xiaomao),
//    STICKER_MIFENG(R.drawable.icon_sticker_xiaomao),
//    STICKER_XIONG(R.drawable.icon_sticker_xiaomao),



    FBStickerResEnum(int iconRes) {this.iconRes = iconRes;}

    //图标地址
    private final int iconRes;

    public Drawable getIcon(Context context) {
        return ContextCompat.getDrawable(context, iconRes);
    }
}
