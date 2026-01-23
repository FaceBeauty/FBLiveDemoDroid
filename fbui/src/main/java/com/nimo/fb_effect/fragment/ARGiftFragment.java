package com.nimo.fb_effect.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.base.FBBaseFragment;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import com.shizhefei.view.indicator.FragmentListPageAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.LayoutBar;
import com.shizhefei.view.viewpager.SViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 礼物
 */
public class ARGiftFragment extends FBBaseFragment {
    private SViewPager fbPager;
    private ScrollIndicatorView indicatorView;
    private IndicatorViewPager indicatorViewPager;
    private IndicatorViewPager.IndicatorFragmentPagerAdapter fragmentPagerAdapter;
    private View container;
    private ImageView ivClean;
    private final List<String> fbTabs = new ArrayList<>();



  @Override protected int getLayoutId() {
    return R.layout.layout_sticker;
  }

  @Override protected void initView(View view, Bundle savedInstanceState) {
      fbPager = view.findViewById(R.id.fb_pager);
      indicatorView = view.findViewById(R.id.indicatorView);
      container = view.findViewById(R.id.container);
      ivClean = view.findViewById(R.id.iv_clean);

      //添加标题
      fbTabs.clear();
      fbTabs.add(requireContext().getString(R.string.gift));

      indicatorView.setSplitAuto(false);

      indicatorViewPager = new IndicatorViewPager(indicatorView, fbPager);
      indicatorViewPager.setIndicatorScrollBar(new LayoutBar(getContext(),
              R.layout.layout_indicator_tab));
      fbPager.setCanScroll(true);
      fbPager.setOffscreenPageLimit(3);
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

              return new GiftFragment();

          }
      };
      indicatorViewPager.setAdapter(fragmentPagerAdapter);
      container.setBackground(ContextCompat.getDrawable(getContext(),
              R.color.dark_background));

      fbPager.setCurrentItem(FBSelectedPosition.POSITION_AR,false);

  }

    @Override public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        FBEffect.shareInstance().setARItem(FBItemEnum.FBItemMask.getValue(), "");
        FBSelectedPosition.POSITION_MASK = -1;
        super.onDestroy();
    }
}
