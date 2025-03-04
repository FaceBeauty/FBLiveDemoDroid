package com.nimo.fb_effect.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.FBPanelLayout;
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

/**
 * 功能——美颜
 */
public class FBBeautyFragment extends FBBaseFragment {


  private SViewPager htPager;
  private ScrollIndicatorView topIndicatorView;
  private Button alternateIndicatorView;
  private IndicatorViewPager indicatorViewPager;
  private IndicatorViewPager.IndicatorFragmentPagerAdapter fragmentPagerAdapter;
  private View container;
  private View line;
  private RelativeLayout bottomLayout;
  private AppCompatImageView returnIv;
  private String which;
  private FBBarView barView;

  public void setBtnShutterClick(BtnShutterClick btnShutterClick) {
    this.btnShutterClick = btnShutterClick;
  }

  private BtnShutterClick btnShutterClick;

  private AppCompatImageView shutterIv;

  private final List<String> htTabs = new ArrayList<>();
  private FBPanelLayout FBPanelLayout;


  @Override
  protected int getLayoutId()  {
    return R.layout.layout_beauty;
  }

  private String getCurrentMode() {
    // 根据你的业务逻辑返回当前模式
    // 这里只是一个示例，你可以根据实际情况修改
    return "NORMAL"; // 示例返回值
  }

  @Override
  protected void initView(View view, Bundle savedInstanceState) {

    htPager = view.findViewById(R.id.fb_pager);
    topIndicatorView = view.findViewById(R.id.top_indicator_view);
    alternateIndicatorView = view.findViewById(R.id.alternate_indicator_view);
    container = view.findViewById(R.id.container);
    line = view.findViewById(R.id.line);
    barView = view.findViewById(R.id.fb_bar);
    bottomLayout = view.findViewById(R.id.rl_bottom);
    returnIv = view.findViewById(R.id.return_iv);
    shutterIv = view.findViewById(R.id.shutter_iv);
    FBPanelLayout = new FBPanelLayout(getContext());

    RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");


    Bundle bundle = this.getArguments();//得到从Activity传来的数据
    if (bundle != null) {
      which = bundle.getString("switch");
    }

    //添加标题
    htTabs.clear();
    htTabs.add(requireContext().getString(R.string.beauty_skin));
    htTabs.add(requireContext().getString(R.string.beauty_shape));
    htTabs.add(requireContext().getString(R.string.filter));

    // topIndicatorView.setSplitMethod(FixedIndicatorView.SPLITMETHOD_WEIGHT);
    // topIndicatorView.setSplitMethod(FixedIndicatorView.SPLITMETHOD_WRAP);
    topIndicatorView.setHorizontalScrollBarEnabled(true);
    // topIndicatorView.set
    indicatorViewPager = new IndicatorViewPager(topIndicatorView, htPager);
    returnIv.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
//        RxBus.get().post(FBEventAction.ACTION_CHANGE_PANEL, FBViewState.MODE);

      }
    });
    barView.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {

      }
    });
    alternateIndicatorView.setOnClickListener  (new OnClickListener() {
      @Override public void onClick(View v) {
        RxBus.get().post(FBEventAction.ACTION_STYLE_SELECTED,"");
      }
    });

    shutterIv.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        //点击拍照
        if (btnShutterClick != null) {
          btnShutterClick.clickShutter();
        }
      }
    });



    htPager.setCanScroll(false);
    htPager.setOffscreenPageLimit(2);


    FBSelectedPosition.POSITION_STICKER = 0;
    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemSticker.getValue(), "");
    RxBus.get().post(FBEventAction.ACTION_SYNC_STICKER_ITEM_CHANGED, "");

    FBSelectedPosition.POSITION_MASK = 0;
    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemMask.getValue(), "");
    RxBus.get().post(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED, "");

    fragmentPagerAdapter = new IndicatorViewPager.IndicatorFragmentPagerAdapter(getChildFragmentManager()) {
      @Override public int getCount() {
        return htTabs.size();
      }

      @Override public View getViewForTab(int position,
                                          View convertView,
                                          ViewGroup container) {
        if (!FBState.isDark) {
          convertView = LayoutInflater.from(getContext())
              .inflate(R.layout.item_fb_tab_white, container, false);
        } else {
          convertView = LayoutInflater.from(getContext())
              .inflate(R.layout.item_fb_tab_dark, container, false);
        }
        ((AppCompatTextView) convertView).setText(htTabs.get(position));
        ((AppCompatTextView) convertView).setTextSize(15);
        return convertView;
      }

      @Override
      public int getItemPosition(Object object) {
        return FragmentListPageAdapter.POSITION_NONE;
      }

      @Override public Fragment getFragmentForPage(int position) {
        Log.e("position:", position + "");
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

    htPager.setCurrentItem(FBSelectedPosition.POSITION_BEAUTY,false);
    changeTheme("");
  }

  public interface BtnShutterClick {
    //点击拍照按钮的回调
    void clickShutter();
  }

  /**
   * 更换主题
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_CHANGE_THEME) })
  public void changeTheme(@Nullable Object o) {
    Log.e("切换主题:", FBState.isDark ? "黑色" : "白色");

    if (FBState.isDark) {
      topIndicatorView.setBackground(ContextCompat.getDrawable(getContext(),
          R.color.dark_background));
      bottomLayout.setBackground(ContextCompat.getDrawable(getContext(),
          R.color.dark_background));

      line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_line));
      returnIv.setSelected(false);

    } else {
      topIndicatorView.setBackground(ContextCompat.getDrawable(getContext(),
          R.color.light_background));
      bottomLayout.setBackground(ContextCompat.getDrawable(getContext(),
          R.color.light_background));

      line.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_line));
      returnIv.setSelected(true);

    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }
}
