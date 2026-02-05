package com.nimo.fb_effect.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.facebeauty.model.FBMakeupEnum;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBFaceShape;
import com.nimo.fb_effect.model.FBFaceShapeKey;
import com.nimo.fb_effect.model.FBFaceShapeReshapeMap;
import com.nimo.fb_effect.model.FBFaceShapeValue;
import com.nimo.fb_effect.model.FBFaceTrim;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBBeautyKey;
import com.nimo.fb_effect.model.FBBeautyParam;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.DpUtils;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBBeautyEnum;
import com.nimo.facebeauty.model.FBFilterEnum;

/**
 * 复用的Slider
 */

public class FBBarView extends LinearLayout implements SeekBar.OnSeekBarChangeListener {

    private TextView fbNumberTV;
    private TextView fbBubbleTV;
    private SeekBar fbSeekBar;
    private View fbProgressV;
    private View fbMiddleV;
    private ImageView fbRenderEnableIV;

    private int fbSeekBarWidth = 0;

    public FBBarView(Context context) {
        super(context);
        init();
    }

    public FBBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FBBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FBBarView init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_bar, this);
        RxBus.get().register(this);
        initView();
        return this;
    }

    /**
     * 初始化View
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        fbNumberTV = findViewById(R.id.fbNumberTV);
        fbBubbleTV = findViewById(R.id.fbBubbleTV);
        fbSeekBar = findViewById(R.id.fbSeekBar);
        fbProgressV = findViewById(R.id.fbProgressV);
        fbMiddleV = findViewById(R.id.fbMiddleV);
        fbRenderEnableIV = findViewById(R.id.fbRenderEnableIV);

        changeTheme(null);
        fbSeekBar.setOnSeekBarChangeListener(this);

        fbRenderEnableIV.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        FBEffect.shareInstance().setRenderEnable(false);
                        RxBus.get().post(FBEventAction.ACTION_RENDER_PICTURE, "");
                        return true;
                    case MotionEvent.ACTION_UP:
                        FBEffect.shareInstance().setRenderEnable(true);
                        RxBus.get().post(FBEventAction.ACTION_RENDER_PICTURE, "");
                        return true;
                }
                return false;
            }
        });

    }

    /**
     * 销毁View时候解绑
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        RxBus.get().unregister(this);
    }

    /**
     * 同步滑动条参数
     */
    @Subscribe(thread = EventThread.MAIN_THREAD,
               tags = { @Tag(FBEventAction.ACTION_SYNC_PROGRESS) })
    public void syncProgress(Object o) {
        Log.e("面板1", FBState.currentViewState.name());
        Log.e("面板2", FBState.currentSecondViewState.name());
        //美妆-口红
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_LIPSTICK) {

            //口红效果未选中，隐藏滑动条
            if (FBUICacheUtils.getMakeupItemPositionCache(FBMakeupEnum.HTMakeupLipstick.getValue()) == 0) {

                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }
            String currentType = FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupLipstick.getValue());
            int progress = FBUICacheUtils
                    .getMakeupItemValueCache(FBMakeupEnum.HTMakeupLipstick.getValue(), currentType);
            fbSeekBar.setProgress(progress);
            styleNormal(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupLipstick.getValue(), currentType));
            return;
        }
        //美妆——眉毛
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_EYEBROW) {

            //美妆效果未选中，隐藏滑动条
            if (FBUICacheUtils.getMakeupItemPositionCache(FBMakeupEnum.HTMakeupEyebrow.getValue()) == 0) {
                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }

            String currentType = FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyebrow.getValue());
            Log.d("eyebrowcurrentName", "syncProgress: " + currentType);
            int progress = FBUICacheUtils
                    .getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyebrow.getValue(), currentType);
            fbSeekBar.setProgress(progress);
            styleNormal(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyebrow.getValue(), currentType));
            return;
        }

        //美妆——腮红
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_BLUSH) {

            //美妆效果未选中，隐藏滑动条
            if (FBUICacheUtils.getMakeupItemPositionCache(FBMakeupEnum.HTMakeupBlush.getValue()) == 0) {
                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }

            String currentType = FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupBlush.getValue());
            int progress = FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupBlush.getValue(), currentType);
            fbSeekBar.setProgress(progress);
            styleNormal(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupBlush.getValue(), currentType));
            return;
        }

        //美妆——眼影
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_EYESHADOW) {

            //美妆效果未选中，隐藏滑动条
            if (FBUICacheUtils.getMakeupItemPositionCache(FBMakeupEnum.HTMakeupEyeshadow.getValue()) == 0) {
                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }
            String currentName = FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyeshadow.getValue());
            int progress = FBUICacheUtils
                    .getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyeshadow.getValue(), currentName);
            fbSeekBar.setProgress(progress);
            styleNormal(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyeshadow.getValue(), currentName));
            return;
        }

        //美妆——眼线
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_EYELINE) {

            if (FBUICacheUtils.getMakeupItemPositionCache(FBMakeupEnum.HTMakeupEyeline.getValue()) == 0) {
                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }

            String currentName = FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyeline.getValue());
            int progress = FBUICacheUtils
                    .getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyeline.getValue(), currentName);
            fbSeekBar.setProgress(progress);
            styleNormal(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyeline.getValue(), currentName));
            return;
        }

        //美妆——睫毛
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_EYELASH) {

            //美妆效果未选中，隐藏滑动条
            if (FBUICacheUtils.getMakeupItemPositionCache(FBMakeupEnum.HTMakeupEyelash.getValue()) == 0) {
                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }

            String currentName = FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyelash.getValue());
            int progress = FBUICacheUtils
                    .getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyelash.getValue(), currentName);
            fbSeekBar.setProgress(progress);
            styleNormal(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyelash.getValue(), currentName));
            return;
        }

        //美妆——美瞳
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_BEAUTYPUPILS) {

            //美妆效果未选中，隐藏滑动条
            if (FBUICacheUtils.getMakeupItemPositionCache(FBMakeupEnum.HTMakeupPupils.getValue()) == 0) {
                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }

            String currentName = FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupPupils.getValue());
            Log.d("pupilscurrentName", "syncProgress: " + currentName);
            int progress = FBUICacheUtils
                    .getMakeupItemValueCache(FBMakeupEnum.HTMakeupPupils.getValue(), currentName);
         
            fbSeekBar.setProgress(progress);
            styleNormal(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupPupils.getValue(), currentName));
            return;
        }
        //轻彩妆
        if (FBState.currentViewState == FBViewState.LIGHT_MAKEUP
                && FBState.currentSecondViewState == FBViewState.LIGHT_MAKEUP) {

            //美发效果未选中，隐藏滑动条
            if (FBUICacheUtils.getLightMakeupPosition() == 0) {

                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }

            int progress = FBUICacheUtils
                    .setLightMakeupValue(FBState.currentLightMakeup.getName());
            fbSeekBar.setProgress(progress);
            styleNormal(FBUICacheUtils.setLightMakeupValue(FBState.currentLightMakeup.getName()));
            return;
        }
        //美发
        if (FBState.currentViewState == FBViewState.BEAUTY_HAIR
                && FBState.currentSecondViewState == FBViewState.BEAUTY_HAIR) {

            //美发效果未选中，隐藏滑动条
            if (FBUICacheUtils.getBeautyHairPosition() == 0) {

                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }

            int progress = FBUICacheUtils
                    .setBeautyHairValue(FBState.currentHair.getName());
            Log.e("当前模块:", FBState.currentHair.getName());
            Log.e("美发滑动参数同步:", progress + "");
            fbSeekBar.setProgress(progress);
            styleNormal(FBUICacheUtils.setBeautyHairValue(FBState.currentHair.getName()));
            return;
        }
        //美颜——美颜
        if (FBState.currentViewState == FBViewState.BEAUTY
            && FBState.currentSecondViewState == FBViewState.BEAUTY_SKIN) {

            //美颜效果未选中，隐藏滑动条
            if (FBState.getCurrentBeautySkin() == FBBeautyKey.NONE) {
                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);

            }

            int progress = FBUICacheUtils.beautySkinValue(FBState.getCurrentBeautySkin());
            Log.e("当前模块:", FBState.getCurrentBeautySkin().name());
            Log.e("美颜滑动参数同步:", progress + "");
            switch (FBState.getCurrentBeautySkin()) {
                // case vague_blurriness:
                // case precise_blurriness:
                case blurriness:
                case whiteness:
                case rosiness:
                case sharpening:
                case clearness:
                case sharpfeatured:
                case undereye_circles:
                case nasolabial:
                case eyeslight:
                case teethwhite:
                case tracker1:
                case tracker2:
                case tracker3:
                    styleNormal(progress);
                    fbSeekBar.setProgress(progress);
                    break;
                case brightness:
                    styleTransform(progress);
                    fbSeekBar.setProgress(progress);
                    break;
                case whitebalance:
                    styleTransform(progress);
                    fbSeekBar.setProgress(progress);
                    break;
                case NONE:
                    setVisibility(INVISIBLE);
                    break;
            }
            return;

        }
        //美颜——脸型

        if (FBState.currentViewState == FBViewState.BEAUTY
                && FBState.currentSecondViewState == FBViewState.FACE_SHAPE) {

            //脸型，默认显示滑动条
            setVisibility(VISIBLE);
//            if (FBState.getCurrentFaceShape() == FBFaceShape.CLASSIC) {
//                setVisibility(INVISIBLE);
//                return;
//            } else {
//                setVisibility(VISIBLE);
//
//            }

//            int progress = FBUICacheUtils.beautySkinValue(FBState.getCurrentBeautySkin());
            int progress = FBUICacheUtils.faceShapeValue(FBState.getCurrentFaceShape());
            Log.e("当前模块:", FBState.getCurrentFaceShape().name());
            Log.e("美颜滑动参数同步:", progress + "");
            switch (FBState.getCurrentFaceShape()) {
                case CLASSIC:
                case SQUARE_FACE:
                case LONG_FACE:
                case ROUND_FACE:
                case THIN_FACE:
                    styleNormal(progress);
                    fbSeekBar.setProgress(progress);
                    break;

            }
            return;

        }

        //美颜——美型
        if (FBState.currentViewState == FBViewState.BEAUTY
            && FBState.currentSecondViewState == FBViewState.BEAUTY_FACE_TRIM) {

            //美型效果未选中，隐藏滑动条
            if (FBUICacheUtils.beautyFaceTrimPosition() == -1) {
                setVisibility(INVISIBLE);
                return;
            } else {
                setVisibility(VISIBLE);
            }

            int progress = FBUICacheUtils
                .beautyFaceTrimValue(FBState.currentFaceTrim);
            Log.e("当前模块:", FBState.currentFaceTrim.name());
            Log.e("美型滑动参数同步:", progress + "");
            fbSeekBar.setProgress(progress);

            //根据参数 选中哪种滑动条
            switch (FBState.currentFaceTrim) {
                case EYE_ENLARGING: //大眼
                case NOSE_ROOT_ENLARGING:  // 山根
                case NOSE_APEX_LESSENING:  //鼻头 *
                case NOSE_THINNING:  //瘦鼻
                case MOUTH_SMILING:  //微笑嘴唇
                case EYE_CORNER_ENLARGING:  // 开眼角
                case CHEEK_SHORTENING:  //短脸
                case TEMPLE_ENLARGING:  //丰太阳穴 *
                case EYE_ROUNDING:  //圆眼
                case CHEEK_THINNING:  //瘦脸
                case CHEEK_V_SHAPING:  //V脸
                case CHEEK_NARROWING: //窄脸
                case CHEEK_BONE_THINNING:  //瘦颧骨
                case JAW_BONE_THINNING:  //瘦下颌骨
                case head_lessening:  //小头
                case FACE_LESSENING:  //小脸
                    styleNormal(progress);
                    break;
                case CHIN_TRIMMING:  //下巴
                case MOUTH_TRIMMING:// 嘴型
                case NOSE_ENLARGING:  //长鼻
                case PHILTRUM_TRIMMING: //缩人中
                case FOREHEAD_TRIM:  //发际线
                case EYE_SAPCING:  //眼间距
                case EYE_CORNER_TRIMMING:  //眼角角度
                    styleTransform(progress);
                    break;
            }

            return;
        }

        //美颜——滤镜
        if (FBState.currentViewState == FBViewState.FILTER) {
            if (FBUICacheUtils.getBeautyFilterPosition() == 0) {
                setVisibility(INVISIBLE);
                return;
            } else {
                if (FBState.currentSliderViewState == FBViewState.FILTER_STYLE) {
                    setVisibility(VISIBLE);
                } else {
                    setVisibility(INVISIBLE);
                }
            }

            // setVisibility(VISIBLE);
            styleNormal(FBUICacheUtils.getBeautyFilterValue(FBState.currentStyleFilter.getName()));
            Log.d("filtername", "syncProgress: " + FBState.currentStyleFilter.getName());
            fbSeekBar.setProgress(FBUICacheUtils.getBeautyFilterValue(FBState.currentStyleFilter.getName()));
            return;
        }

        if (FBState.currentViewState == FBViewState.BEAUTY) {
            if (FBUICacheUtils.getBeautyFilterPosition() == 0) {
                setVisibility(INVISIBLE);
                return;
            } else {
                if (FBState.currentSliderViewState == FBViewState.FILTER_STYLE) {
                    setVisibility(VISIBLE);
                } else {
                    setVisibility(INVISIBLE);
                }
            }

            // setVisibility(VISIBLE);
//            styleNormal(FBUICacheUtils.getBeautyFilterValue(FBState.currentStyleFilter.getName()));
            styleNormal(FBUICacheUtils.getBeautyFilterValue(FBUICacheUtils.getBeautyFilterName()));
            Log.d("filtername", "syncProgress: " + FBState.currentStyleFilter.getName());
            fbSeekBar.setProgress(FBUICacheUtils.getBeautyFilterValue(FBUICacheUtils.getBeautyFilterName()));
            return;
        }
        if (FBState.currentViewState == FBViewState.HIDE) {
            setVisibility(GONE);
        }


    }

    /**
     * 根据系统主题切换面板
     *
     * @param o
     */
    @Subscribe(thread = EventThread.MAIN_THREAD,
               tags = { @Tag(FBEventAction.ACTION_CHANGE_THEME) })
    public void changeTheme(Object o) {
        Drawable bgThumb = ContextCompat.getDrawable(getContext(), R.drawable.bg_fb_seekbar_thumb);
        Drawable bgMiddle = ContextCompat.getDrawable(getContext(), R.drawable.bg_middle);
        Drawable bgProgress = ContextCompat.getDrawable(getContext(), R.drawable.bg_fb_bar_progress);

        if (FBState.isDark) {
            DrawableCompat.setTint(bgMiddle, ContextCompat.getColor(getContext(),
                R.color.white));
            DrawableCompat.setTint(bgProgress, ContextCompat.getColor(getContext(),
                R.color.theme_color));
            DrawableCompat.setTint(bgThumb, ContextCompat.getColor(getContext(),
                R.color.theme_color));

            fbBubbleTV.setTextColor(ContextCompat.getColor(getContext(),
                R.color.seekbar_background));

            fbRenderEnableIV.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_render_white_enable));
        } else {

            DrawableCompat.setTint(bgMiddle, ContextCompat.getColor(getContext(),
                R.color.dark_black));
            DrawableCompat.setTint(bgProgress, ContextCompat.getColor(getContext(),
                R.color.theme_color));
            DrawableCompat.setTint(bgThumb, ContextCompat.getColor(getContext(),
                R.color.theme_color));
            fbBubbleTV.setTextColor(ContextCompat.getColor(getContext(),
                R.color.seekbar_background));

            fbRenderEnableIV.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.ic_render_black_enable));
        }
        fbProgressV.setBackground(bgProgress);
        fbMiddleV.setBackground(bgMiddle);
        fbSeekBar.setThumb(bgThumb);
    }

    @SuppressLint("LongLogTag")
    @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        RxBus.get().post(FBEventAction.ACTION_RENDER_PHOTO, true);

        //美颜——美肤——美肤
        if (FBState.currentViewState == FBViewState.BEAUTY
            && FBState.currentSecondViewState == FBViewState.BEAUTY_SKIN) {

            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.beautySkinResetEnable()) {
                FBUICacheUtils.beautySkinResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            switch (FBState.getCurrentBeautySkin()) {

                case blurriness:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyClearSmoothing.getValue(), progress);
                    break;
                case whiteness:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautySkinWhitening.getValue(), progress);
                    break;
                case rosiness:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautySkinRosiness.getValue(), progress);
                    break;
                case sharpening:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyImageSharpness.getValue(), progress);
                    break;
                case clearness:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyImageClarity.getValue(), progress);
                    break;
                case sharpfeatured:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyFaceContouring.getValue(), progress);
                    break;
                case brightness:
                    styleTransform(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyImageBrightness.getValue(), progress - 50);
                    break;
                case undereye_circles:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyDarkCircleLessening.getValue(), progress);
                    break;
                case nasolabial:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyNasolabialLessening.getValue(), progress);
                    break;
                case whitebalance:
                    styleTransform(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyWhiteBalance.getValue(), progress - 50);
                    break;
                case teethwhite:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyToothWhitening.getValue(), progress);
                    break;
                case eyeslight:
                    styleNormal(progress);
                    FBEffect.shareInstance().setBeauty(FBBeautyEnum.FBBeautyEyeBrightening.getValue(), progress);
                    break;
                case tracker1:
                    styleNormal(progress);
                    //todo
                    break;
                case tracker2:
                    styleNormal(progress);
                    //todo
                    break;
                case tracker3:
                    styleNormal(progress);
                    //todo
                    break;
                case NONE:
                    break;
            }


            Log.e("美颜" + FBState.getCurrentBeautySkin(), progress + "");
            FBUICacheUtils.beautySkinValue(FBState.getCurrentBeautySkin(), progress);

            return;
        }
        //美颜——脸型
        if (FBState.currentViewState == FBViewState.BEAUTY
                && FBState.currentSecondViewState == FBViewState.FACE_SHAPE) {

            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.faceShapeResetEnable()) {
                FBUICacheUtils.faceShapeResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            switch (FBState.getCurrentFaceShape()) {
                case CLASSIC://100
                    styleNormal(progress);
                    applyFaceShape(FBFaceShapeValue.CLASSIC_FACE_SHAPE,progress);
                    break;
                case SQUARE_FACE:
                    styleNormal(progress);
                    applyFaceShape(FBFaceShapeValue.SQUARE_FACE_SHAPE,progress);
                    break;
                case LONG_FACE:
                    styleNormal(progress);
                    applyFaceShape(FBFaceShapeValue.LONG_FACE_SHAPE,progress);
                    break;
                case ROUND_FACE:
                    styleNormal(progress);
                    applyFaceShape(FBFaceShapeValue.ROUND_FACE_SHAPE,progress);
                    break;
                case THIN_FACE:
                    styleNormal(progress);
                    applyFaceShape(FBFaceShapeValue.THIN_FACE_SHAPE,progress);
                    break;

            }


            Log.e("脸型" + FBState.getCurrentFaceShape(), progress + "");
            FBUICacheUtils.faceShapeValue(FBState.getCurrentFaceShape(), progress);

            return;
        }
        //美颜——美肤——美型
        if (FBState.currentViewState == FBViewState.BEAUTY
            && FBState.currentSecondViewState == FBViewState.BEAUTY_FACE_TRIM) {

            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.beautyFaceTrimResetEnable()) {
                FBUICacheUtils.beautyFaceTrimResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            switch (FBState.currentFaceTrim) {
                case EYE_ENLARGING: //大眼
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeEnlarging, progress);
                    styleNormal(progress);
                    break;
                case EYE_ROUNDING:  //圆眼
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeRounding, progress);
                    styleNormal(progress);
                    break;
                case CHEEK_THINNING:  //瘦脸
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekThinning, progress);
                    styleNormal(progress);
                    break;
                case CHEEK_V_SHAPING:  //V脸
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekVShaping, progress);
                    styleNormal(progress);
                    break;
                case CHEEK_NARROWING: //窄脸
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekNarrowing, progress);
                    styleNormal(progress);
                    break;
                case CHEEK_BONE_THINNING:  //瘦颧骨
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekboneThinning, progress);
                    styleNormal(progress);
                    break;
                case JAW_BONE_THINNING:  //瘦下颌骨
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeJawboneThinning, progress);
                    styleNormal(progress);
                    break;
                case TEMPLE_ENLARGING:  //丰太阳穴 *
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeTempleEnlarging, progress);
                    styleNormal(progress);
                    break;
                case head_lessening:  //小头
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeHeadLessening, progress);
                    styleNormal(progress);
                    break;
                case FACE_LESSENING:  //小脸
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeFaceLessening, progress);
                    styleNormal(progress);
                    break;
                case CHEEK_SHORTENING:  //短脸
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeCheekShortening, progress);
                    styleNormal(progress);
                    break;
                case CHIN_TRIMMING:  //下巴
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeChinTrimming, progress - 50);
                    styleTransform(progress);
                    break;
                case PHILTRUM_TRIMMING: //缩人中
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapePhiltrumTrimming, progress - 50);
                    styleTransform(progress);
                    break;
                case FOREHEAD_TRIM:  //发际线
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeForeheadTrimming, progress - 50);
                    styleTransform(progress);
                    break;
                case EYE_SAPCING:  //眼间距
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeSpaceTrimming, progress - 50);
                    styleTransform(progress);
                    break;
                case EYE_CORNER_TRIMMING:  //眼角角度
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeCornerTrimming, progress - 50);
                    styleTransform(progress);
                    break;
                case EYE_CORNER_ENLARGING:  // 开眼角
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeEyeCornerEnlarging, progress);
                    styleNormal(progress);
                    break;
                case NOSE_ENLARGING:  //长鼻
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeNoseEnlarging, progress - 50);
                    styleTransform(progress);
                    break;
                case NOSE_THINNING:  //瘦鼻
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeNoseThinning, progress);
                    styleNormal(progress);
                    break;
                case NOSE_APEX_LESSENING:  //鼻头 *
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeNoseApexLessening, progress);
                    styleNormal(progress);
                    break;
                case NOSE_ROOT_ENLARGING:  // 山根
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeNoseRootEnlarging, progress);
                    styleNormal(progress);
                    break;
                case MOUTH_TRIMMING:// 嘴型
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeMouthTrimming, progress - 50);
                    styleTransform(progress);
                    break;
                case MOUTH_SMILING:  //微笑嘴唇
                    styleNormal(progress);
                    FBEffect.shareInstance().setReshape(FBBeautyParam.FBReshapeMouthSmiling, progress);
                    break;
            }

            Log.e("美型" + FBState.currentFaceTrim, progress + "");
            FBUICacheUtils.beautyFaceTrimValue(FBState.currentFaceTrim, progress);

            return;
        }
        //美发
        if (FBState.currentViewState == FBViewState.BEAUTY_HAIR
                && FBState.currentSecondViewState == FBViewState.BEAUTY_HAIR) {

            styleNormal(progress);
            Log.e("美发" + FBState.currentHair.getName(), progress + "%");
            FBUICacheUtils.setBeautyHairValue(FBState.currentHair.getName(), progress);

            FBEffect.shareInstance().setHairStyling(FBState.currentHair.getId(), progress);
            return;
        }
        //轻彩妆
        if (FBState.currentViewState == FBViewState.LIGHT_MAKEUP
                && FBState.currentSecondViewState == FBViewState.LIGHT_MAKEUP) {

            styleNormal(progress);
            FBUICacheUtils.setLightMakeupValue(FBState.currentLightMakeup.getName(), progress);
            FBEffect.shareInstance().setStyle(FBState.currentLightMakeup.getName(),progress);
            return;
        }
        //美妆——口红
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_LIPSTICK) {

            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.beautyMakeUpResetEnable()) {
                FBUICacheUtils.beautyMakeUpResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            styleNormal(progress);
            // 设置口红参数缓存
            FBUICacheUtils.setMakeupItemValueCache(FBMakeupEnum.HTMakeupLipstick.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupLipstick.getValue()), progress);
            FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupLipstick.getValue(), "value", Integer.toString(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupLipstick.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupLipstick.getValue()))));
            return;
        }
        //美妆——眉毛
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_EYEBROW) {

            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.beautyMakeUpResetEnable()) {
                FBUICacheUtils.beautyMakeUpResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            styleNormal(progress);
            FBUICacheUtils.setMakeupItemValueCache(FBMakeupEnum.HTMakeupEyebrow.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyebrow.getValue()), progress);
            FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupEyebrow.getValue(), "value", Integer.toString(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyebrow.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyebrow.getValue()))));
            return;
        }

        //美妆——腮红
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_BLUSH) {

            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.beautyMakeUpResetEnable()) {
                FBUICacheUtils.beautyMakeUpResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            styleNormal(progress);
            FBUICacheUtils.setMakeupItemValueCache(FBMakeupEnum.HTMakeupBlush.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupBlush.getValue()), progress);
            FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupBlush.getValue(), "value", Integer.toString(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupBlush.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupBlush.getValue()))));
            return;
        }

        //美妆——眼影
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_EYESHADOW) {

            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.beautyMakeUpResetEnable()) {
                FBUICacheUtils.beautyMakeUpResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            styleNormal(progress);
            FBUICacheUtils.setMakeupItemValueCache(FBMakeupEnum.HTMakeupEyeshadow.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyeshadow.getValue()), progress);
            FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupEyeshadow.getValue(), "value", Integer.toString(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyeshadow.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyeshadow.getValue()))));
            return;
        }

        //美妆——眼线
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_EYELINE) {

            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.beautyMakeUpResetEnable()) {
                FBUICacheUtils.beautyMakeUpResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            styleNormal(progress);
            FBUICacheUtils.setMakeupItemValueCache(FBMakeupEnum.HTMakeupEyeline.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyeline.getValue()), progress);
            FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupEyeline.getValue(), "value", Integer.toString(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyeline.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyeline.getValue()))));

            return;
        }

        //美妆——睫毛
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_EYELASH) {
            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.beautyMakeUpResetEnable()) {
                FBUICacheUtils.beautyMakeUpResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            styleNormal(progress);
            FBUICacheUtils.setMakeupItemValueCache(FBMakeupEnum.HTMakeupEyelash.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyelash.getValue()), progress);
            FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupEyelash.getValue(), "value", Integer.toString(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupEyelash.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupEyelash.getValue()))));

            return;
        }

        //美妆——美瞳
        if (FBState.currentViewState == FBViewState.BEAUTY_MAKE_UP
                && FBState.currentSecondViewState == FBViewState.MAKEUP_BEAUTYPUPILS) {

            //滑动条变化时，将重置按钮设为可选
            if (!FBUICacheUtils.beautyMakeUpResetEnable()) {
                FBUICacheUtils.beautyMakeUpResetEnable(true);
                RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "");
            }

            styleNormal(progress);
            FBUICacheUtils.setMakeupItemValueCache(FBMakeupEnum.HTMakeupPupils.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupPupils.getValue()), progress);
            FBEffect.shareInstance().setMakeup(FBMakeupEnum.HTMakeupPupils.getValue(), "value", Integer.toString(FBUICacheUtils.getMakeupItemValueCache(FBMakeupEnum.HTMakeupPupils.getValue(), FBUICacheUtils.getMakeupItemNameOrTypeCache(FBMakeupEnum.HTMakeupPupils.getValue()))));
            return;
        }



        //美颜——滤镜
//        if (FBState.currentViewState == FBViewState.FILTER) {
//
//            styleNormal(progress);
//            Log.e("滤镜" + FBState.currentStyleFilter.getName(), progress + "%");
//            FBUICacheUtils.setBeautyFilterValue(FBState.currentStyleFilter.getName(), progress);
//
//            FBEffect.shareInstance().setFilter(FBFilterEnum.FBFilterBeauty.getValue(), FBState.currentStyleFilter.getName(), progress);
//            return;
//        }

        if (FBState.currentViewState == FBViewState.BEAUTY) {

            styleNormal(progress);
            Log.e("滤镜" + FBState.currentStyleFilter.getName(), progress + "%");

            FBEffect.shareInstance().setFilter(FBFilterEnum.FBFilterBeauty.getValue(), FBUICacheUtils.getBeautyFilterName(), progress);
            FBUICacheUtils.setBeautyFilterValue(FBUICacheUtils.getBeautyFilterName(), progress);
            return;
        }

    }

    /**
     * 开始拖动时显示数字
     *
     * @param seekBar
     */
    @Override public void onStartTrackingTouch(SeekBar seekBar) {
        fbBubbleTV.setVisibility(View.VISIBLE);

    }

    /**
     * 停止拖动时隐藏数字
     *
     * @param seekBar
     */
    @Override public void onStopTrackingTouch(SeekBar seekBar) {
        fbBubbleTV.setVisibility(View.GONE);
        RxBus.get().post(FBEventAction.ACTION_SYNC_ITEM_CHANGED, "");
    }

    /**
     * 滑动到该参数,参数区域 0~100
     *
     * @param progress
     */
    private void styleNormal(final int progress) {
        fbMiddleV.setVisibility(GONE);

        final CharSequence percent = new StringBuilder().append(progress).append("");

        fbNumberTV.setText(percent);

        //防止第一次获取不到mtSeekBar的宽度

        if (fbSeekBarWidth <= 0) {
            fbSeekBar.post(new Runnable() {
                @Override
                public void run() {
                    fbSeekBarWidth = fbSeekBar.getWidth();

                    float width = fbSeekBar.getWidth() - (DpUtils.dip2px(34) + 0.5f);

                    fbBubbleTV.setText(percent);
                    fbBubbleTV.setX(width / 100 * progress + (DpUtils.dip2px(1) + 0.5f));

                    fbProgressV.setVisibility(VISIBLE);
                    ViewGroup.LayoutParams layoutParams = fbProgressV.getLayoutParams();
                    layoutParams.width = (int) (width / 100 * progress);
                    fbProgressV.setLayoutParams(layoutParams);
                }
            });
        } else {
            float width = fbSeekBarWidth - (DpUtils.dip2px(34) + 0.5f);

            fbBubbleTV.setText(percent);
            fbBubbleTV.setX(width / 100 * progress + (DpUtils.dip2px(1) + 0.5f));

            fbProgressV.setVisibility(VISIBLE);
            ViewGroup.LayoutParams layoutParams = fbProgressV.getLayoutParams();
            fbProgressV.setX(getContext().getResources().getDisplayMetrics().density * 16f + 0.5f);
            layoutParams.width = (int) (width / 100 * progress);
            fbProgressV.setLayoutParams(layoutParams);
        }

    }

    /**
     * 滑动到该参数,参数区域 -50~50
     *
     * @param progress
     */
    private void styleTransform(int progress) {
        fbMiddleV.setVisibility((progress > 48 && progress < 52) ? GONE : VISIBLE);

        CharSequence percent = new StringBuilder().append(progress - 50);

        fbNumberTV.setText(percent);

        float width = fbSeekBar.getWidth() - (DpUtils.dip2px(34));

        fbBubbleTV.setText(percent);
        fbBubbleTV.setX(width / 100 * progress + (DpUtils.dip2px(1)));

        fbProgressV.setVisibility(VISIBLE);
        ViewGroup.LayoutParams layoutParams = fbProgressV.getLayoutParams();

        if (progress < 51) {
            fbProgressV.setX(width / 100 * progress + (DpUtils.dip2px(16)));
            layoutParams.width = (int) (width * (50 - progress) / 100);
        } else {
            fbProgressV.setX(width / 2 + (DpUtils.dip2px(16)));
            layoutParams.width = (int) (width * (progress - 50) / 100);
        }

        fbProgressV.setLayoutParams(layoutParams);
    }
    private int mapReshape(int progress, int max) {
        float p = progress / 100f;
        return (int) (p * p * max);
    }
    private int mapByRatio(int progress, int targetValue) {
        float p = progress / 100f;
        return Math.round(targetValue * p);
    }
    public static void applyFaceShape(
            FBFaceShapeValue shape,
            int progress
    ) {
        FBEffect effect = FBEffect.shareInstance();

        for (FBFaceShapeReshapeMap map : FBFaceShapeReshapeMap.values()) {

            int baseValue = map.extractor.apply(shape);

            int value = Math.round(baseValue * (progress / 50f));

            effect.setReshape(map.reshapeParam, value);

            int uiValue = map.uiNeedOffset50 ? value + 50 : value;

            FBUICacheUtils.beautyFaceTrimValue(
                    map.faceTrim,
                    uiValue
            );
        }
    }




}
