package com.nimo.fb_effect.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.adapter.FBFaceTrimItemViewBinder;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBFaceTrim;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.utils.MyItemDecoration;
import com.nimo.fb_effect.view.FBResetDialog;
import java.util.Arrays;
import java.util.List;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * 美颜——美肤——美型
 */
@SuppressWarnings("unused")
public class FBFaceTrimFragment extends FBBaseLazyFragment {

  private final MultiTypeAdapter adapter = new MultiTypeAdapter();

  private final List<FBFaceTrim> mData;

  private android.widget.ImageView ivReset;
  private android.widget.TextView tvReset;
  private LinearLayout btnReset;
  private LinearLayout container;

  private View line;

  private final FBResetDialog resetDialog = new FBResetDialog();

  {
    mData = Arrays.asList(FBFaceTrim.values());
  }

  @Override protected int getLayoutId() {
    return R.layout.fragment_beauty_face_trim;
  }

  @Override protected void initView(
      View view,
      Bundle savedInstanceState) {

    btnReset = findViewById(R.id.btn_reset);
    container = findViewById(R.id.container);
    RecyclerView rvFaceTrim = findViewById(R.id.rv_skin);
    rvFaceTrim.setHasFixedSize(true);
    ivReset = findViewById(R.id.iv_reset);
    tvReset = findViewById(R.id.tv_reset);
    line = findViewById(R.id.line);

    adapter.setItems(mData);

    adapter.register(FBFaceTrim.class, new FBFaceTrimItemViewBinder());

    rvFaceTrim.addItemDecoration(new
        MyItemDecoration(5)
    );

    rvFaceTrim.setAdapter(adapter);

    btnReset.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        resetDialog.show(getChildFragmentManager(), "face_trim");
      }
    });

    changeTheme("");


  }


  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_SYNC_ITEM_CHANGED) })
  public void changedPoint(Object o) {
    adapter.notifyItemChanged(FBUICacheUtils.beautyFaceTrimPosition());
  }

  /**
   * 当前页面显示时的回调
   */
  @SuppressLint("NotifyDataSetChanged")
  @Override protected void onFragmentStartLazy() {
    super.onFragmentStartLazy();
    //更新ui状态
    FBState.currentSecondViewState = FBViewState.BEAUTY_FACE_TRIM;
    //同步滑动条
    RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");
    adapter.notifyDataSetChanged();
    syncReset("");
  }

  /**
   * 同步重置按钮状态
   * @param message
   * support版本Rxbus
   * 传入boolean类型会导致接收不到参数
   */
  @SuppressLint("NotifyDataSetChanged")
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_SYNC_RESET) })
  public void syncReset(String message) {

    btnReset.setEnabled(FBUICacheUtils.beautyFaceTrimResetEnable());
    ivReset.setEnabled(FBUICacheUtils.beautyFaceTrimResetEnable());
    tvReset.setEnabled(FBUICacheUtils.beautyFaceTrimResetEnable());

    if (message.equals("true")) {
      adapter.notifyDataSetChanged();
    }
  }

  /**
   * 切换主题的通知
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
    adapter.notifyDataSetChanged();
  }
}