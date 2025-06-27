package com.nimo.fb_effect.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.FBBeauty;
import com.nimo.fb_effect.model.FBBeautyKey;
import com.nimo.fb_effect.utils.FBUICacheUtils;

/**
 * 美颜——美肤——美肤的Item
 */
@SuppressWarnings("unused")
public class FBSkinItem extends LinearLayout {

  public final @NonNull AppCompatTextView text;

  public final @NonNull AppCompatImageView image;

  public final @NonNull View point;

  private FBBeauty fbBeauty;

  private int initialValue = 0;

  public void init(FBBeauty FBBeauty) {
    this.fbBeauty = FBBeauty;
    if (FBBeauty == FBBeauty.brightness || FBBeauty == FBBeauty.whitebalance) {
      initialValue = 50;
    }
    updateData();
    changedPoint("");
  }

  public FBSkinItem(Context context) {
    super(context);
  }

  public FBSkinItem(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FBSkinItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  {
    LayoutInflater.from(getContext()).inflate(R.layout.item_drawable_top_button, this);
    RxBus.get().register(this);
    text = findViewById(R.id.fbTextTV);
    image = findViewById(R.id.fbImageIV);
    point = findViewById(R.id.point);

    //同步滑动条
    RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");



    setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {

        if (v.isSelected()) {
          return;
        } else {
          FBState.setCurrentBeautySkin(fbBeauty.getFBBeautyKey());
          v.setSelected(true);
        }

        RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");

      }
    });

  }

  /**
   * 将匹配上的美肤Item设为选中状态
   * @param o
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_SYNC_PROGRESS) })
  public void syncState(Object o) {
    setSelected(fbBeauty
        .getFBBeautyKey().name()
        .equals(FBState.getCurrentBeautySkin().name()));
//    Log.d("beautyName", "syncState: "+FBState.getCurrentBeautySkin().name());
  }

  /**
   * 根据主题修改UI
   */
  public void updateData() {
    if (FBState.isDark) {
      text.setText((fbBeauty.getName(getContext())));
      image.setImageDrawable(ContextCompat
          .getDrawable(getContext(),
              fbBeauty.getDrawableRes_white()));
      text.setTextColor(
          ContextCompat.getColorStateList(getContext(),
              R.color.color_selector_tab_dark));
    } else {
      text.setText((fbBeauty.getName(getContext())));
      image.setImageDrawable(ContextCompat
          .getDrawable(getContext(),
              fbBeauty.getDrawableRes_black()));
      text.setTextColor(
          ContextCompat.getColorStateList(getContext(),
              R.color.color_selector_tab_light));
    }
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
   * 根据参数判断当前是否显示圆点
   * @param o
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_SYNC_ITEM_CHANGED) })
  public void changedPoint(Object o) {
    point.setVisibility((FBUICacheUtils
        .beautySkinValue(FBBeautyKey.valueOf(fbBeauty.name())) != initialValue) ?
                        View.VISIBLE : View.INVISIBLE);
  }

}
