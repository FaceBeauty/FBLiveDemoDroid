package com.nimo.fb_effect.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import com.nimo.fb_effect.FBPanelLayout;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.base.FBBaseFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.view.FBBarView;
import com.shizhefei.view.indicator.FragmentListPageAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.viewpager.SViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能——美发
 */
public class FBBeautyHairFragment extends FBBaseFragment {


    private SViewPager htPager;
    private ScrollIndicatorView topIndicatorView;
//    private Button alternateIndicatorView;
    private IndicatorViewPager indicatorViewPager;
    private IndicatorViewPager.IndicatorFragmentPagerAdapter fragmentPagerAdapter;
    private View container;
//    private View line;
    private RelativeLayout bottomLayout;
    private AppCompatImageView returnIv;
    private String which;
    private FBBarView barView;

    private final List<String> htTabs = new ArrayList<>();
    private FBPanelLayout HTPanelLayout;
    private LinearLayout containerall;

    private ConstraintLayout fragmentRoot;
    private SparseBooleanArray pageVisibilityMap = new SparseBooleanArray();
    private int currentPosition = FBSelectedPosition.POSITION_HAIR;

    @Override
    protected int getLayoutId()  {
        return R.layout.layout_beauty;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        htPager = view.findViewById(R.id.fb_pager);
        topIndicatorView = view.findViewById(R.id.top_indicator_view);
//        alternateIndicatorView = view.findViewById(R.id.alternate_indicator_view);
        container = view.findViewById(R.id.container);
//        line = view.findViewById(R.id.line);
        barView = view.findViewById(R.id.fb_bar);
        bottomLayout = view.findViewById(R.id.rl_bottom);
        //todo s
        containerall = view.findViewById(R.id.container_all);
        fragmentRoot = view.findViewById(R.id.root_layout);
        //todo e
//        returnIv = view.findViewById(R.id.return_iv);
        HTPanelLayout = new FBPanelLayout(getContext());
        FBState.currentViewState = FBViewState.BEAUTY_HAIR;
        RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");


        Bundle bundle = this.getArguments();//得到从Activity传来的数据
        if (bundle != null) {
            which = bundle.getString("switch");
        }

        //添加标题
        htTabs.clear();
        htTabs.add(requireContext().getString(R.string.beauty_hair));
//        topIndicatorView.setHorizontalScrollBarEnabled(true);
        // topIndicatorView.set
        indicatorViewPager = new IndicatorViewPager(topIndicatorView, htPager);
        /*returnIv.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                RxBus.get().post(FBEventAction.ACTION_CHANGE_PANEL, FBViewState.MODE);

            }
        });*/
        barView.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {

            }
        });
        /*alternateIndicatorView.setOnClickListener  (new OnClickListener() {
            @Override public void onClick(View v) {
                RxBus.get().post(FBEventAction.ACTION_STYLE_SELECTED,"");
            }
        });*/
        htPager.setCanScroll(false);
        htPager.setOffscreenPageLimit(1);
        FBSelectedPosition.POSITION_HAIR = 0;
//        FBEffect.shareInstance().setARItem(FBItemEnum.FBItemHa.getValue(), "");
        RxBus.get().post(FBEventAction.ACTION_SYNC_HAIR_ITEM_CHANGED, "");
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
                boolean isSelected = (position == currentPosition) &&
                        (containerall.getVisibility() == View.VISIBLE);
//                ((AppCompatTextView) convertView).setText(htTabs.get(position));
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
                        return new FBHairFragment();
                    default:
                        return new FBHairFragment();
                }
            }
        };
        indicatorViewPager.setAdapter(fragmentPagerAdapter);
        htPager.addOnPageChangeListener(new SViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                currentPosition = position;
                updateContainerVisibility(position);
            }
        });

        htPager.setCurrentItem(FBSelectedPosition.POSITION_HAIR,false);
        changeTheme("");

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

        } else {
            topIndicatorView.setBackground(ContextCompat.getDrawable(getContext(),
                    R.color.light_background));
            bottomLayout.setBackground(ContextCompat.getDrawable(getContext(),
                    R.color.light_background));
        }
    }
    private void updateContainerVisibility(int position) {
        boolean shouldHide = pageVisibilityMap.get(position);
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
    private void refreshTabs() {
        // 强制刷新所有标签视图
        if (indicatorViewPager != null ) {
            indicatorViewPager.notifyDataSetChanged();
        }
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FBUICacheUtils.setBeautyHairPosition(0);
        FBEffect.shareInstance().setHairStyling(0,0);
    }
}
