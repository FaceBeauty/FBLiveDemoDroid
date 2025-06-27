package com.nimo.fb_effect.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.FBPanelLayout;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.fb_effect.utils.SharedPreferencesUtil;
import com.shizhefei.view.indicator.FragmentListPageAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.viewpager.SViewPager;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.base.FBBaseFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import com.nimo.fb_effect.view.FBBarView;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;

import java.util.ArrayList;
import java.util.List;



public class FBBeautyFragment extends FBBaseFragment {

  private SViewPager htPager;
  private ScrollIndicatorView topIndicatorView;
  private IndicatorViewPager indicatorViewPager;
  private IndicatorViewPager.IndicatorFragmentPagerAdapter fragmentPagerAdapter;
  private View container;
  private RelativeLayout bottomLayout;
  private LinearLayout containerall;

  private ConstraintLayout fragmentRoot;

  private String which;
  private FBBarView barView;
  private final List<String> htTabs = new ArrayList<>();
  private FBPanelLayout FBPanelLayout;

  // 新增状态管理
  private SparseBooleanArray pageVisibilityMap = new SparseBooleanArray();
  private int currentPosition = FBSelectedPosition.POSITION_BEAUTY;

  @Override
  protected int getLayoutId()  {
    return R.layout.layout_beauty;
  }

  @Override
  protected void initView(View view, Bundle savedInstanceState) {
    htPager = view.findViewById(R.id.fb_pager);
    topIndicatorView = view.findViewById(R.id.top_indicator_view);
    container = view.findViewById(R.id.container);
    barView = view.findViewById(R.id.fb_bar);
    bottomLayout = view.findViewById(R.id.rl_bottom);
    containerall = view.findViewById(R.id.container_all);
    fragmentRoot = view.findViewById(R.id.root_layout);
    FBPanelLayout = new FBPanelLayout(getContext());

    SharedPreferencesUtil.init(getContext());
    FBConfigTools.getInstance().initFBConfigTools(getContext());
    FBState.currentViewState = FBViewState.BEAUTY;

    RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");

    Bundle bundle = this.getArguments();
    if (bundle != null) {
      which = bundle.getString("switch");
    }

    // 初始化页面可见状态
    for (int i = 0; i < 3; i++) {
      pageVisibilityMap.put(i, true);
    }

    //添加标题
    htTabs.clear();
    htTabs.add(requireContext().getString(R.string.beauty_skin));
    htTabs.add(requireContext().getString(R.string.beauty_shape));
    htTabs.add(requireContext().getString(R.string.filter));

    topIndicatorView.setHorizontalScrollBarEnabled(true);
    indicatorViewPager = new IndicatorViewPager(topIndicatorView, htPager);

    barView.setOnClickListener(v -> {});

    htPager.setCanScroll(false);
    htPager.setOffscreenPageLimit(3);

    FBSelectedPosition.POSITION_STICKER = 0;
    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemSticker.getValue(), "");
    RxBus.get().post(FBEventAction.ACTION_SYNC_STICKER_ITEM_CHANGED, "");

    FBSelectedPosition.POSITION_MASK = 0;
    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemMask.getValue(), "");
    RxBus.get().post(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED, "");


    fragmentPagerAdapter = new IndicatorViewPager.IndicatorFragmentPagerAdapter(getChildFragmentManager()) {
      @Override
      public int getCount() {
        return htTabs.size();
      }


      @Override
      public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
          convertView = LayoutInflater.from(getContext())
                  .inflate(R.layout.item_fb_tab_dynamic, container, false);
        }

        AppCompatTextView tabView = (AppCompatTextView) convertView;
        boolean isSelected = (position == currentPosition) &&
                (containerall.getVisibility() == View.VISIBLE);

        // 设置样式
        tabView.setTextColor(ContextCompat.getColor(getContext(),
                isSelected ? R.color.fb_selected : R.color.fb_unselected));

        tabView.setText(htTabs.get(position));
        tabView.setTextSize(15);
        tabView.setOnClickListener(v -> handleTabClick(position));
        return convertView;
      }



      @Override
      public int getItemPosition(Object object) {
        return FragmentListPageAdapter.POSITION_UNCHANGED;
      }

      @Override
      public Fragment getFragmentForPage(int position) {
        Log.e("position:", position + "");
        // 根据位置返回Fragment
        switch (position) {
          case 1:
            return new FBFaceTrimFragment();
          case 2:
            return new FBBeautyFilterFragment();
          default:
            return new FBBeautySkinFragment();
        }
      }
    };



    indicatorViewPager.setAdapter(fragmentPagerAdapter);

    // 添加页面切换监听
    htPager.addOnPageChangeListener(new SViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {

        currentPosition = position;
        updateContainerVisibility(position);
      }
    });

//    htPager.setCurrentItem(FBSelectedPosition.POSITION_BEAUTY, false);
    changeTheme("");
  }

  private void refreshTabs() {
    // 强制刷新所有标签视图
    if (indicatorViewPager != null ) {
      indicatorViewPager.notifyDataSetChanged();
    }
  }


  private void handleTabClick(int position) {
    if (position == currentPosition) {
      // 切换当前页面的可见状态
      boolean newState = !pageVisibilityMap.get(position);
      pageVisibilityMap.put(position, newState);
      updateContainerVisibility(position);
    } else {
      // 切换到新页面时强制显示
      pageVisibilityMap.put(position, false);
      htPager.setCurrentItem(position, false);
    }
    refreshTabs();

  }

  private void updateContainerVisibility(int position) {
    boolean shouldHide = pageVisibilityMap.get(position);
//      containerall.setVisibility(shouldHide ? View.GONE : View.VISIBLE);
//      refreshTabs();
      if (shouldHide) {
        containerall.animate()
                .translationY(containerall.getHeight())
                .setDuration(200)
                .withEndAction(() -> {
                  containerall.setVisibility(View.GONE);
                  refreshTabs();
                })
                .start();
      } else {
        containerall.setVisibility(View.VISIBLE);
        containerall.setTranslationY(containerall.getHeight());
        containerall.animate()
                .translationY(0)
                .setDuration(200)
                .withEndAction(() -> refreshTabs())
                .start();
      }
  }

  @Subscribe(thread = EventThread.MAIN_THREAD,
          tags = { @Tag(FBEventAction.ACTION_CHANGE_THEME) })
  public void changeTheme(@Nullable Object o) {
    Log.e("切换主题:", FBState.isDark ? "黑色" : "白色");

    if (FBState.isDark) {
      topIndicatorView.setBackground(ContextCompat.getDrawable(getContext(),
              R.color.dark_background));
      bottomLayout.setBackground(ContextCompat.getDrawable(getContext(),
              R.color.dark_background));
    } else {
      topIndicatorView.setBackground(ContextCompat.getDrawable(getContext(),
              R.color.light_background));
      bottomLayout.setBackground(ContextCompat.getDrawable(getContext(),
              R.color.light_background));
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }
}