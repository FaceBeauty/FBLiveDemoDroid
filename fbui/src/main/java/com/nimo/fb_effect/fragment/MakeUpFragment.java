package com.nimo.fb_effect.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.facebeauty.FBEffect;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.adapter.MakeUpViewBinder;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.MakeUpEnum;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.DpUtils;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.utils.MyItemDecoration;
import com.nimo.fb_effect.view.FBBarView;
import com.nimo.fb_effect.view.FBResetDialog;
import java.util.Arrays;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * 美妆
 */
@SuppressWarnings("unused")
public class MakeUpFragment extends FBBaseLazyFragment implements MakeUpItemFragment.OnMakeUpItemBackListener{
    private OnMakeUpPanelChangeListener panelChangeListener;
    private LinearLayout btnReset;
    private LinearLayout container;
    private android.widget.ImageView ivReset;
    private android.widget.TextView tvReset;
    private  RecyclerView rvMakeUp;

    private final FBResetDialog resetDialog = new FBResetDialog();

    private View line;

    private final MultiTypeAdapter adapter = new MultiTypeAdapter();
    private List<MakeUpEnum> mData;
    {
        mData = Arrays.asList(MakeUpEnum.values());
    }
    public MakeUpFragment() {
        this.mData = mData;
    }

    @Override protected int getLayoutId() {
        return R.layout.fragment_face_shape;
    }

    @Override protected void initView(
            View view,
            Bundle savedInstanceState) {

        btnReset = findViewById(R.id.btn_reset);
        container = findViewById(R.id.container);
        rvMakeUp = findViewById(R.id.rv_faceShape);
        rvMakeUp.setHasFixedSize(true);
        ivReset = findViewById(R.id.iv_reset);
        tvReset = findViewById(R.id.tv_reset);
        line = findViewById(R.id.line);
        adapter.setItems(mData);

        adapter.register(MakeUpEnum.class, new MakeUpViewBinder());

        rvMakeUp.addItemDecoration(new
                MyItemDecoration(5)
        );

        rvMakeUp.setAdapter(adapter);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                resetDialog.show(getChildFragmentManager(), "face_trim");
//                resetDialog.show(getChildFragmentManager(), "beauty_makeup");
            }
        });

        changeTheme("");

    }


    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = { @Tag(FBEventAction.ACTION_SYNC_ITEM_CHANGED) })
    public void changedPoint(Object o) {
        adapter.notifyItemChanged(FBUICacheUtils.makeupPosition());
//        adapter.notifyItemChanged(FBUICacheUtils.faceShapePosition())
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        //更新ui状态
//        FBState.currentSecondViewState = FBViewState.BEAUTY_MAKE_UP;
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
        btnReset.setEnabled(FBUICacheUtils.beautyMakeUpResetEnable());
        ivReset.setEnabled(FBUICacheUtils.beautyMakeUpResetEnable());
        tvReset.setEnabled(FBUICacheUtils.beautyMakeUpResetEnable());
        if (message.equals("true")) {
            adapter.notifyDataSetChanged();
        }

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
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = { @Tag(FBEventAction.ACTION_CHANGE_PANEL) }
    )
    public void onChangeMakeUpPanel(FBViewState state) {
        switch (state) {
            case MAKEUP_LIPSTICK:
                FBState.currentSecondViewState = FBViewState.MAKEUP_LIPSTICK;
                switchMakeupItem("lipstick");
                break;

            case MAKEUP_EYEBROW:
                FBState.currentSecondViewState = FBViewState.MAKEUP_EYEBROW;
                switchMakeupItem("eyebrow");
                break;

            case MAKEUP_BLUSH:
                FBState.currentSecondViewState = FBViewState.MAKEUP_BLUSH;
                switchMakeupItem("blush");
                break;
            case MAKEUP_EYESHADOW:
                FBState.currentSecondViewState = FBViewState.MAKEUP_EYESHADOW;
                switchMakeupItem("eyeshadow");
                break;
            case MAKEUP_EYELINE:
                FBState.currentSecondViewState = FBViewState.MAKEUP_EYELINE;
                switchMakeupItem("eyeline");
                break;
            case MAKEUP_EYELASH:
                FBState.currentSecondViewState = FBViewState.MAKEUP_EYELASH;
                switchMakeupItem("eyelash");
                break;
            case MAKEUP_BEAUTYPUPILS:
                FBState.currentSecondViewState = FBViewState.MAKEUP_BEAUTYPUPILS;
                switchMakeupItem("pupils");
                break;
        }
    }
    private void switchMakeupItem(String type) {
        MakeUpItemFragment fragment = new MakeUpItemFragment();
        btnReset.setVisibility(GONE);
        line.setVisibility(GONE);
        changePanelHeight(200);
        Bundle bundle = new Bundle();
        bundle.putString("switch", type);
        fragment.setArguments(bundle);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("MakeUpItem")
                .commitAllowingStateLoss();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Fragment parent = getParentFragment();
        if (parent instanceof OnMakeUpPanelChangeListener) {
            panelChangeListener = (OnMakeUpPanelChangeListener) parent;
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        panelChangeListener = null;
    }
    private void changePanelHeight(int dp) {
        if (panelChangeListener != null) {
            panelChangeListener.onRequestChangePagerHeight(
                    DpUtils.dp2px(dp)
            );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FBEffect.shareInstance().setMakeup(0, "type", "-1");//口红
        FBEffect.shareInstance().setMakeup(1, "type", "-1");//眉毛
        FBEffect.shareInstance().setMakeup(2, "type", "-1");//腮红

        FBEffect.shareInstance().setMakeup(3, "name", "");//眼影
        FBEffect.shareInstance().setMakeup(4, "name", "");//眼线
        FBEffect.shareInstance().setMakeup(5, "name", "");//睫毛
        FBEffect.shareInstance().setMakeup(6, "name", "");//美瞳
    }

    @Override
    public void onMakeUpItemBack() {
        btnReset.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        changePanelHeight(110);

    }


    public interface OnMakeUpPanelChangeListener {
        void onRequestChangePagerHeight(int heightPx);
    }
}
