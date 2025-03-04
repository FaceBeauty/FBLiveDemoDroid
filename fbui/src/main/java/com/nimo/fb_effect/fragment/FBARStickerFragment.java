package com.nimo.fb_effect.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.shizhefei.view.indicator.FragmentListPageAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.LayoutBar;
import com.shizhefei.view.viewpager.SViewPager;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.base.FBBaseFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * AR道具
 */
public class FBARStickerFragment extends FBBaseFragment {
  private SViewPager fbPager;
  private ScrollIndicatorView indicatorView;
  private IndicatorViewPager indicatorViewPager;
  private IndicatorViewPager.IndicatorFragmentPagerAdapter fragmentPagerAdapter;
  private View container;
//  private View line;
//  private View divide;
  private ImageView ivClean;

  private final List<String> fbTabs = new ArrayList<>();

  @Override protected int getLayoutId() {
    return R.layout.layout_sticker;
  }

  @Override protected void initView(View view, Bundle savedInstanceState) {
    fbPager = view.findViewById(R.id.fb_pager);
    indicatorView = view.findViewById(R.id.indicatorView);
    container = view.findViewById(R.id.container);
//    line = view.findViewById(R.id.line);
//    divide = view.findViewById(R.id.divide);
    ivClean = view.findViewById(R.id.iv_clean);

    //添加标题
    fbTabs.clear();
    fbTabs.add(requireContext().getString(R.string.sticker));

    indicatorView.setSplitAuto(false);

    indicatorViewPager = new IndicatorViewPager(indicatorView, fbPager);
    indicatorViewPager.setIndicatorScrollBar(new LayoutBar(getContext(),
        R.layout.layout_indicator_tab));
    fbPager.setCanScroll(true);
    fbPager.setOffscreenPageLimit(3);


    //恢复美颜默认值
    FBUICacheUtils.resetSkinValue(getContext());
    FBUICacheUtils.beautySkinResetEnable(false);

    FBUICacheUtils.resetFaceTrimValue(getContext());
    FBUICacheUtils.beautyFaceTrimResetEnable(false);

    FBSelectedPosition.POSITION_STICKER = 0;
    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemSticker.getValue(), "");
    RxBus.get().post(FBEventAction.ACTION_SYNC_STICKER_ITEM_CHANGED, "");

    FBSelectedPosition.POSITION_MASK = 0;
    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemMask.getValue(), "");
    RxBus.get().post(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED, "");


    fragmentPagerAdapter = new IndicatorViewPager.IndicatorFragmentPagerAdapter(getChildFragmentManager()) {
      @Override public int getCount() {
        return fbTabs.size();
      }

      @Override public View getViewForTab(int position,
                                          View convertView,
                                          ViewGroup container) {
          convertView = LayoutInflater.from(getContext())
              .inflate(R.layout.item_fb_sticker_tab, container, false);


        ((AppCompatTextView) convertView).setText(fbTabs.get(position));
        return convertView;
      }

      @Override
      public int getItemPosition(Object object) {
        return FragmentListPageAdapter.POSITION_NONE;
      }

      @Override public Fragment getFragmentForPage(int position) {
        Log.e("position:", position + "");

        switch (position) {
          case 0:
            return new FBStickerFragment();
          default:
            return new FBStickerFragment();
        }
      }
    };
    indicatorViewPager.setAdapter(fragmentPagerAdapter);
    container.setBackground(ContextCompat.getDrawable(getContext(),
        R.color.dark_background));

//    line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.horizonal_line));
//    divide.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.divide_line));
    fbPager.setCurrentItem(FBSelectedPosition.POSITION_AR,false);
    // changeTheme("");

  }
  /**
   * 更换主题
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_CHANGE_THEME) })
  public void changeTheme(@Nullable Object o) {
    Log.e("切换主题:", FBState.isDark ? "黑色" : "白色");

    if (FBState.isDark) {
      container.setBackground(ContextCompat.getDrawable(getContext(),
          R.color.dark_background));

//      line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.divide_line));

    } else {
      container.setBackground(ContextCompat.getDrawable(getContext(),
          R.color.light_background));

//      line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_line));

    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }
}