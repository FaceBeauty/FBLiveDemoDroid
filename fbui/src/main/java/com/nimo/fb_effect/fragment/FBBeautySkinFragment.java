package com.nimo.fb_effect.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBBeauty;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.view.FBResetDialog;
import com.nimo.fb_effect.view.FBSkinItem;
import java.util.ArrayList;

/**
 * 美颜——美肤——美肤
 */
@SuppressWarnings("unused")
public class FBBeautySkinFragment extends FBBaseLazyFragment {

  private LinearLayout btnReset;
  private LinearLayout container;
  private android.widget.ImageView ivReset;
  private android.widget.TextView tvReset;

  private final FBResetDialog resetDialog = new FBResetDialog();

  private View line;

  private FBSkinItem btnWhite;
  private FBSkinItem btnBlur;
  private FBSkinItem btnRosiness;
  private FBSkinItem btnClearness;
  private FBSkinItem btnBrightness;
  private FBSkinItem btnUnderEyes;
  private FBSkinItem btnNasolabial;
  private FBSkinItem btnEyesLight;
  private FBSkinItem btnTeethWhite;
  private FBSkinItem btnTracker1;
  private FBSkinItem btnTracker2;
  private FBSkinItem btnTracker3;

  private ArrayList<FBSkinItem> itemViews = new ArrayList<>();

  @Override protected int getLayoutId() {
    return R.layout.fragment_beauty_skin;
  }

  @Override protected void initView(
      View view,
      Bundle savedInstanceState) {

    btnReset = findViewById(R.id.btn_reset);
    ivReset = findViewById(R.id.iv_reset);
    tvReset = findViewById(R.id.tv_reset);

    container = findViewById(R.id.container);

    line = findViewById(R.id.line);

    btnWhite = findViewById(R.id.btn_white);
    btnWhite.init(FBBeauty.whiteness);
    btnBlur = findViewById(R.id.btn_blur);
    btnBlur.init(FBBeauty.blurriness);
    btnRosiness = findViewById(R.id.btn_rosiness);
    btnRosiness.init(FBBeauty.rosiness);
    btnClearness = findViewById(R.id.btn_clearness);
    btnClearness.init(FBBeauty.clearness);
    btnBrightness = findViewById(R.id.btn_brightness);
    btnBrightness.init(FBBeauty.brightness);
    btnUnderEyes = findViewById(R.id.btn_undereye_circles);
    btnUnderEyes.init(FBBeauty.undereye_circles);
    btnNasolabial = findViewById(R.id.btn_nasolabial);
    btnNasolabial.init(FBBeauty.nasolabial);

    btnEyesLight = findViewById(R.id.btn_eyeslight);
    btnEyesLight.init(FBBeauty.eyeslight);
    btnTeethWhite = findViewById(R.id.btn_teethwhite);
    btnTeethWhite.init(FBBeauty.teethwhite);
    btnTracker1 = findViewById(R.id.btn_tracker1);
    btnTracker1.init(FBBeauty.tracker1);
    btnTracker2 = findViewById(R.id.btn_tracker2);
    btnTracker2.init(FBBeauty.tracker2);
    btnTracker3 = findViewById(R.id.btn_tracker3);
    btnTracker3.init(FBBeauty.tracker3);

    btnEyesLight.setVisibility(View.GONE);
    btnTeethWhite.setVisibility(View.GONE);
    btnTracker1.setVisibility(View.GONE);
    btnTracker2.setVisibility(View.GONE);
    btnTracker3.setVisibility(View.GONE);

    itemViews.add(btnWhite);
    itemViews.add(btnRosiness);
    itemViews.add(btnClearness);
    itemViews.add(btnBrightness);
    itemViews.add(btnUnderEyes);
    itemViews.add(btnNasolabial);
    // itemViews.add(btnTeethWhite);
    // itemViews.add(btnEyesLight);
    // itemViews.add(btnTracker1);
    // itemViews.add(btnTracker2);
    // itemViews.add(btnTracker3);

    btnReset.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        resetDialog.show(getChildFragmentManager(), "skin");
      }
    });

    changeTheme("");

  }


  @SuppressLint("NotifyDataSetChanged")
  @Override protected void onFragmentStartLazy() {
    super.onFragmentStartLazy();
    //更新ui状态
    FBState.currentSecondViewState = FBViewState.BEAUTY_SKIN;
    //同步滑动条
    RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");
    syncReset("");
  }

  /**
   * 同步重置按钮状态
   *
   * @param message support版本Rxbus
   * 传入boolean类型会导致接收不到参数
   */
  @SuppressLint("NotifyDataSetChanged")
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_SYNC_RESET) })
  public void syncReset(String message) {
    btnReset.setEnabled(FBUICacheUtils.beautySkinResetEnable());
    ivReset.setEnabled(FBUICacheUtils.beautySkinResetEnable());
    tvReset.setEnabled(FBUICacheUtils.beautySkinResetEnable());

    if (message.equals("true")) {
      for (FBSkinItem item : itemViews) {
        item.updateData();
      }
    }

    // btnBlur.update();
    RxBus.get().post(FBEventAction.ACTION_SYNC_ITEM_CHANGED, "");


  }

  /**
   * 切换主题
   *
   * @param o
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_CHANGE_THEME) })
  public void changeTheme(Object o) {
    if (FBState.isDark) {
      container.setBackground(ContextCompat.getDrawable(getContext(), R.color.dark_background));
      line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.divide_line));
      ivReset.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_reset_white));
      tvReset.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_reset_text_white));
    } else {
      container.setBackground(ContextCompat.getDrawable(getContext(), R.color.light_background));
      line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.light_gray_line));
      ivReset.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_reset_black));
      tvReset.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_reset_text_black));
    }
  }



}
