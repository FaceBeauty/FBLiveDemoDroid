package com.nimo.fb_effect;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.fragment.FBARMaskFragment;
import com.nimo.fb_effect.fragment.FBARStickerFragment;
import com.nimo.fb_effect.fragment.FBBeautyFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.DpUtils;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.utils.SharedPreferencesUtil;
import com.nimo.fb_effect.view.FBResetAllDialog;
import com.nimo.fb_effect.view.FBTakephotoButton;
import com.nimo.fb_effect.view.FBTakephotoButton.OnProgressTouchListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import com.nimo.stickerview.StickerView;

import static com.nimo.fb_effect.utils.FBUICacheUtils.previewInitialHeight;
import static com.nimo.fb_effect.utils.FBUICacheUtils.previewInitialWidth;

/**
 * 美颜面板主体
 * 使用美颜面板请务必调用init()方法
 */
@SuppressWarnings("unused")
public class FBPanelLayout extends ConstraintLayout
    implements ValueAnimator.AnimatorUpdateListener {

  private AppCompatImageView ivFbTrigger;
  private AppCompatImageView ivFbRestore;

  private final FBResetAllDialog resetAllDialog = new FBResetAllDialog();

  private final ValueAnimator showAnim =
      ValueAnimator.ofFloat(DpUtils.dip2px(320), 0);
  private final ValueAnimator hideAnim =
      ValueAnimator.ofFloat(0, DpUtils.dip2px(320));
  private final ValueAnimator takePhotoAnim =
      ValueAnimator.ofFloat(DpUtils.dip2px(320), 0);

  //show filter tip timer
  private Timer showFilterTipTimer;

  private FragmentManager fm;

  public void setBtnShutterClick(BtnShutterClick btnShutterClick) {
    this.btnShutterClick = btnShutterClick;
  }
  public void setBtnShutterLongClick(BtnShutterLongClick btnShutterLongClick) {
    this.btnShutterLongClick = btnShutterLongClick;
  }

  private BtnShutterClick btnShutterClick;
  private BtnShutterLongClick btnShutterLongClick;

  private View controllerView;
  private AppCompatImageView btnShutter;
  private AppCompatImageView ivReturn;
  private FBTakephotoButton btnShutter1;
  private AppCompatImageView shutterIv;
  private AppCompatTextView tvFilterType;
  private TextView tiInteractionHint;

  private StickerView stickerView;
  private DisplayMetrics dm;
  private int[] mWatermarkSize;
  private boolean isWaterMarkFocus = false;
//  private boolean isForThreed = false;



  public FBPanelLayout(@NonNull Context context) {
    super(context);
  }

  public FBPanelLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FBPanelLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  /**
   * 使用时务必调用
   *
   * @param fm 初始化必须
   * @return 当前view
   */
  public FBPanelLayout init(FragmentManager fm) {
    this.fm = fm;
    SharedPreferencesUtil.init(getContext());
    RxBus.get().register(this);
    FBConfigTools.getInstance().initFBConfigTools(getContext());
    checkVersion();
    initView();
    initData();
    return this;
  }

  {
    //初始化动画参数
    showAnim.setDuration(250);
    hideAnim.setDuration(250);
    showAnim.setInterpolator(new AccelerateInterpolator());
    hideAnim.setInterpolator(new LinearInterpolator());
    showAnim.addUpdateListener(FBPanelLayout.this);
    hideAnim.addUpdateListener(FBPanelLayout.this);

    takePhotoAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        btnShutter.postOnAnimation(new Runnable() {
          @Override public void run() {
            btnShutter.setTranslationY((Float) valueAnimator.getAnimatedValue());
          }
        });
      }
    });

    takePhotoAnim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {

      }

      //根据状态显示不同的拍照按钮
      @Override public void onAnimationEnd(Animator animator) {
        if (FBState.isDark && ((Float)
            (takePhotoAnim.getAnimatedValue())) == 0F) {
          //黑色主题 面板隐藏
          btnShutter.setImageDrawable(ContextCompat
              .getDrawable(getContext(), R.mipmap.ic_shutter_dark));
        } else if (FBState.isDark && ((Float)
            (takePhotoAnim.getAnimatedValue())) != 0F) {
          //黑色主题 面板开启
          btnShutter.setImageDrawable(ContextCompat
              .getDrawable(getContext(), R.drawable.ic_shutter_dark));
        } else if (!FBState.isDark && FBState.currentViewState == FBViewState.HIDE) {
          btnShutter.setImageDrawable(ContextCompat
              //白色主题 面板隐藏
              .getDrawable(getContext(), R.mipmap.icon_shutter_light));
        } else {
          //白色主题 面板显示
          btnShutter.setImageDrawable(ContextCompat
              .getDrawable(getContext(), R.mipmap.icon_shutter_light));
        }
      }

      @Override public void onAnimationCancel(Animator animator) {

      }

      @Override public void onAnimationRepeat(Animator animator) {

      }
    });

    takePhotoAnim.setDuration(300);
    //和面板的动画插值器不同来实现差值动画
    takePhotoAnim.setInterpolator(new LinearInterpolator());
  }

  /**
   * 初始化View
   */
  private void initView() {
    LayoutInflater.from(getContext()).inflate(R.layout.layout_fb_panel, this);
    ivFbTrigger = findViewById(R.id.iv_fb_trigger);
    ivFbRestore = findViewById(R.id.iv_fb_restore);
    controllerView = findViewById(R.id.controller_view);
    btnShutter = findViewById(R.id.btn_shutter);
    btnShutter1 = findViewById(R.id.btn_shutter1);
    shutterIv = findViewById(R.id.shutter_iv);
    ivReturn = findViewById(R.id.return_iv);
    tvFilterType = findViewById(R.id.tv_filter_tip);
    tiInteractionHint = findViewById(R.id.interaction_hint);
    stickerView = findViewById(R.id.sl_sticker_layout);
    stickerView.setMinStickerSizeScale(0.9f);
    //初始化View的时候记得把美颜的参数进行应用
    FBUICacheUtils.initCache(true);
    //获取屏幕宽高
    dm = getContext().getResources().getDisplayMetrics();

//    initWatermark();

  }


  ///版本检测
  private void checkVersion() {
    String curVersion = "2.1.0";

    // String oldVersion = KvUtils.get().getString("mt_version");

    // if (oldVersion.equals("") || Float.parseFloat(curVersion) > Float.parseFloat(oldVersion)) {
    //
    // }
  }

//  public void setIsThreed(boolean isForThreed){
//    this.isForThreed = isForThreed;
//  }

  //view被销毁时
  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    float[] points = stickerView.getFinalDst();
    FBSelectedPosition.WATERMARK_POINTS = points;
    RxBus.get().unregister(this);
  }

  //切换面板
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_SHOW_FILTER) })
  public void showFilterTip(Object o) {
    if (showFilterTipTimer != null) {
      showFilterTipTimer.cancel();
    }
    tvFilterType.setText(FBState.currentStyleFilter.getTitle());
    tvFilterType.setVisibility(VISIBLE);
    showFilterTipTimer = new Timer();
    showFilterTipTimer.schedule(new TimerTask() {
      @Override public void run() {
        tvFilterType.post(new Runnable() {
          @Override public void run() {
            tvFilterType.setVisibility(GONE);
          }
        });
      }
    }, 800);

  }

  /**
   * 初始化数据源
   */
  private void initData() {
    changeTheme(null);
//    replaceView(new FBModeFragment(),"");
    // btnShutter.setOnClickListener(new OnClickListener() {
    //   @Override public void onClick(View v) {
    //     //点击拍照
    //     if (btnShutterClick != null) {
    //       btnShutterClick.clickShutter();
    //     }
    //   }
    // });

    btnShutter1.setOnProgressTouchListener(new OnProgressTouchListener() {
      @Override public void onClick(FBTakephotoButton photoButton) {
        if (btnShutterClick != null) {
          btnShutterClick.clickShutter();
        }
      }

      @Override public void onLongClick(FBTakephotoButton photoButton) {

        if (btnShutterLongClick != null) {
                  btnShutterLongClick.startVideo();
                }
        btnShutter1.start();
      }

      @Override public void onLongClickUp(FBTakephotoButton photoButton) {
              if (btnShutterLongClick != null) {
                btnShutterLongClick.stopVideo();
              }


      }

      @Override public void onFinish() {

      }
    });

    // btnShutter.setOnTouchListener(new OnTouchListener() {
    //   @Override public boolean onTouch(View v, MotionEvent event) {
    //     if(event.getAction() == MotionEvent.ACTION_DOWN) {
    //       if (btnShutterLongClick != null) {
    //         btnShutterLongClick.startVideo();
    //       }
    //     } else if (event.getAction() == MotionEvent.ACTION_UP) {
    //       if (btnShutterLongClick != null) {
    //         btnShutterLongClick.stopVideo();
    //       }
    //     }
    //     return true;
    //   }
    // });

    shutterIv.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        //点击拍照
        if (btnShutterClick != null) {
          btnShutterClick.clickShutter();
        }
      }
    });

    ivReturn.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        //
      }
    });


    ivFbRestore.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        resetAllDialog.show(fm, "all");
      }
    });


    stickerView.setOnTouchListener(new OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            isWaterMarkFocus = false;
        isWaterMarkFocus = stickerView.getIsFocus();
        if(event.getAction() == MotionEvent.ACTION_UP && isWaterMarkFocus == false){
          Log.e("click","--||--");
          backContainer();
          return true;
        }

        return false;
      }
    });
    // stickerView.setOnClickListener(new OnClickListener() {
    //   @Override public void onClick(View v) {
    //     backContainer();
    //   }
    // });
    //空白处隐藏面板
    setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        v.performClick();

        Log.e("click","--||--");
        backContainer();

        return false;
      }
    });
  }


  //开始按下按钮动画
  public void startButtonAnima(){
    AnimatorSet animatorSet = new AnimatorSet();//组合动画
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(shutterIv, "scaleX", 1f, 1.3f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(shutterIv, "scaleY", 1f, 1.3f);
    animatorSet.setDuration(100);
    animatorSet.setInterpolator(new LinearInterpolator());
    animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
    animatorSet.start();
  }

  //停止按下按钮动画
  public void stopButtonAnima() {
    AnimatorSet animatorSet = new AnimatorSet();//组合动画
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(shutterIv, "scaleX", 1.3f, 1f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(shutterIv, "scaleY", 1.3f, 1f);

    animatorSet.setDuration(100);
    animatorSet.setInterpolator(new LinearInterpolator());
    animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
    animatorSet.start();
  }

  /**
   * 返回上级View
   */
  private void backContainer() {
    Log.e("backFrom", FBState.currentViewState.name());
    switch (FBState.currentViewState) {
//      case HIDE:
      case BEAUTY:
      case STICKER:
      case MASK:
        return;
    }
  }

  /**
   * 隐藏面板
   */
  private void hideContainer() {
    //如果已经隐藏,则不进行动作
    if (FBState.currentViewState == FBViewState.HIDE) return;
    FBState.currentViewState = FBViewState.HIDE;

    //先停止按钮动画
    // if (takePhotoAnim.isRunning()) {
    //   takePhotoAnim.cancel();
    // }
    // takePhotoAnim.setFloatValues(btnShutter.getTranslationY(), DpUtils.dip2px(0));
    // takePhotoAnim.start();

    //动画完成后再显示美颜按钮,避免重叠
    hideAnim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animation) {

      }

      @Override public void onAnimationEnd(Animator animation) {
        ivFbTrigger.setVisibility(View.VISIBLE);
        ivFbRestore.setVisibility(View.VISIBLE);
        btnShutter.setVisibility(View.VISIBLE);
        stickerView.setVisibility(View.GONE);
        //动画监听器用完记得回收,避免内存泄漏
        hideAnim.removeListener(this);
      }

      @Override public void onAnimationCancel(Animator animation) {

      }

      @Override public void onAnimationRepeat(Animator animation) {

      }
    });

    //打断当前的动画,设置新的参数地点,达成打断效果
    if (showAnim.isRunning()) {
      showAnim.cancel();
      hideAnim.setFloatValues(controllerView.getTranslationY(),
          DpUtils.dip2px(320));
      hideAnim.start();
      return;
    }

    hideAnim.setFloatValues(controllerView.getTranslationY(),
        DpUtils.dip2px(320));

    hideAnim.start();
  }

  /**
   * 切换主题
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_CHANGE_THEME) })
  public void changeTheme(Object o) {
    if (FBState.isDark) {
      btnShutter.setImageDrawable(ContextCompat
          .getDrawable(getContext(), R.drawable.icon_home_shutter_dark));
      ivFbRestore.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.icon_restore_all_black));
      ivFbTrigger.setImageDrawable(ContextCompat
          .getDrawable(getContext(), R.mipmap.ic_trigger_white));
    } else {
      btnShutter.setImageDrawable(ContextCompat
          .getDrawable(getContext(), R.drawable.icon_home_shutter_light));
      ivFbRestore.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.icon_restore_all_white));
      ivFbTrigger.setImageDrawable(ContextCompat
          .getDrawable(getContext(), R.mipmap.ic_trigger_black));
    }
  }

  public void setWatermarkSize(int[] watermarkSize){
    mWatermarkSize = watermarkSize;

  }


  //切换面板
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_CHANGE_PANEL) })
  public void showPanel(FBViewState viewState) {
    //根据切换的面板做对应的处理
    Log.e("change_Panel:", viewState.name());
    switch (viewState) {
//      case HIDE:
//        hideContainer();
//        stickerView.setVisibility(GONE);
//        break;

      case BEAUTY:
        ivFbTrigger.setVisibility(View.GONE);
        ivFbRestore.setVisibility(View.GONE);
        shutterIv.setVisibility(View.VISIBLE);
        btnShutter.setVisibility(View.GONE);
        stickerView.setVisibility(VISIBLE);
        switchModePanel(new FBBeautyFragment(),"beauty");
        FBState.currentViewState = viewState;
        FBState.currentSecondViewState = FBViewState.BEAUTY_SKIN;
        Log.e("--Beauty--",viewState.name());
        //setTakePhotoAnim(-320);
        break;

      case STICKER:
        ivFbTrigger.setVisibility(GONE);
        ivFbRestore.setVisibility(GONE);
        shutterIv.setVisibility(View.GONE);
        btnShutter.setVisibility(View.GONE);
        stickerView.setVisibility(VISIBLE);
        switchModePanel(new FBARStickerFragment(),"sticker");
        FBState.currentViewState = viewState;
        FBState.currentSecondViewState = FBViewState.STICKER;
        Log.e("--sticker--",viewState.name());
        //setTakePhotoAnim(-280);
        break;

      case MASK:
        ivFbTrigger.setVisibility(GONE);
        ivFbRestore.setVisibility(GONE);
        shutterIv.setVisibility(View.GONE);
        btnShutter.setVisibility(View.GONE);
        stickerView.setVisibility(VISIBLE);
        switchModePanel(new FBARMaskFragment(),"mask");
        FBState.currentViewState = viewState;
        FBState.currentSecondViewState = FBViewState.MASK;
        Log.e("--mask--",viewState.name());
        //setTakePhotoAnim(-280);
        break;

    }

  }

  /**
   * 展示提示语句
   * @param hint
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_SHOW_GESTURE) })
  public void showHint(String hint) {
    tiInteractionHint.setVisibility(TextUtils.isEmpty(hint) ? View.GONE : View.VISIBLE);
    tiInteractionHint.setText(hint);
  }


  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_REMOVE_STICKER_RECT) })
  public void removeRect(Object o) {
    stickerView.clearSticker();
  }



  /**
   * 设置面板动画
   * @param dpValue
   */
  private void setTakePhotoAnim(float dpValue){
    if (takePhotoAnim.isRunning()) {
      takePhotoAnim.cancel();
    }
    takePhotoAnim.setFloatValues(btnShutter.getTranslationY(), DpUtils.dip2px(dpValue));
    takePhotoAnim.start();
  }

  /**
   * 显示功能面板
   */
  private void showModePanel() {
    if (hideAnim.isRunning()) {
      hideAnim.cancel();
    }
    if (showAnim.isRunning()) {
      showAnim.cancel();
    }
    showAnim.setFloatValues(controllerView.getTranslationY(), 0);

    showAnim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {
//        replaceView(new FBModeFragment(),"");
        btnShutter.setVisibility(View.GONE);
        shutterIv.setVisibility(View.GONE);
        showAnim.removeListener(this);
      }

      @Override public void onAnimationEnd(Animator animator) {

      }

      @Override public void onAnimationCancel(Animator animator) {

      }

      @Override public void onAnimationRepeat(Animator animator) {

      }
    });
    showAnim.start();
  }

  /**
   * 切换子View面板
   *
   * @param fragment 被切换的片段
   */
  private void switchModePanel(@Nullable Fragment fragment, String which) {

    if (hideAnim.isRunning()) {
      hideAnim.cancel();
    }

    if (showAnim.isRunning()) {
      showAnim.cancel();
    }

    hideAnim.setFloatValues(controllerView.getTranslationY(),
        DpUtils.dip2px(330));

    hideAnim.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(Animator animator) {

        //此处禁用点击空白处隐藏面板，否则在第三层面板中快速点击空白区域，第一层和第二层的面板会重合
        setOnTouchListener(null);

      }

      @Override public void onAnimationEnd(Animator animator) {
        showAnim.setFloatValues(controllerView.getTranslationY(), 0F);
        if (fragment != null) {
          replaceView(fragment,which);
        }
        showAnim.start();
        hideAnim.removeListener(this);

        stickerView.setOnTouchListener(new OnTouchListener() {
          @Override public boolean onTouch(View v, MotionEvent event) {
            // isWaterMarkFocus = stickerView.getIsFocus();
            isWaterMarkFocus = false;
            isWaterMarkFocus = stickerView.getIsFocus();
            v.performClick();
            if(event.getAction() == MotionEvent.ACTION_UP && isWaterMarkFocus == false ){
              Log.e("click","--||--");
              backContainer();
              return true;
            }
            return false;
          }
        });
        // stickerView.setOnClickListener(new OnClickListener() {
        //   @Override public void onClick(View v) {
        //     backContainer();
        //   }
        // });

        // 此前点击空白处隐藏面板被禁用，此处需放开
        setOnTouchListener(new OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
            v.performClick();

            Log.e("click","--||--");
            backContainer();

            return false;
          }
        });
      }

      @Override public void onAnimationCancel(Animator animator) {

      }

      @Override public void onAnimationRepeat(Animator animator) {


      }
    });

    hideAnim.start();

  }

  @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
    controllerView.postOnAnimation(new Runnable() {
      @Override public void run() {

        float offsetY = (float) valueAnimator.getAnimatedValue();

        controllerView.setTranslationY(offsetY);
      }
    });
  }
  /**
   * 获取资源路径
   *
   * @param context 上下文
   * @return 资源路径
   */
  public static String getResPath(Context context) {
    String path = null;
    File dataDir = context.getApplicationContext().getFilesDir();
    if (dataDir != null) {
      path = dataDir.getAbsolutePath();
    }
    return path;
  }

  /**
   * 切换子view
   *
   * @param fragment 被切换的碎片
   */
  private void replaceView(Fragment fragment, String which) {

    FragmentTransaction ft = fm.beginTransaction();
    Bundle bundle = new Bundle();
    //设置数据
    bundle.putString("switch", which);
    //绑定 Fragment
    fragment.setArguments(bundle);

    ft.replace(R.id.container_fragment, fragment);

    ft.commitAllowingStateLoss();
  }

  public interface BtnShutterClick {
    //点击拍照按钮的回调
    void clickShutter();
  }
  public interface BtnShutterLongClick {

    void startVideo();
    void stopVideo();
  }

}







