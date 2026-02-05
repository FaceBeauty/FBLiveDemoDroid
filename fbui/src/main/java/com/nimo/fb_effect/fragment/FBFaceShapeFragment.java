package com.nimo.fb_effect.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.adapter.FBFaceShapeItemViewBinder;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBFaceShape;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.utils.MyItemDecoration;
import com.nimo.fb_effect.view.FBResetDialog;
import java.util.Arrays;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * 美颜——脸型
 */
@SuppressWarnings("unused")
public class FBFaceShapeFragment extends FBBaseLazyFragment {

    private LinearLayout btnReset;
    private LinearLayout container;
    private android.widget.ImageView ivReset;
    private android.widget.TextView tvReset;

    private final FBResetDialog resetDialog = new FBResetDialog();

    private View line;

    private final MultiTypeAdapter adapter = new MultiTypeAdapter();
    private List<FBFaceShape> mData;
    {
        mData = Arrays.asList(FBFaceShape.values());
    }
    public FBFaceShapeFragment() {
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
        RecyclerView rvFaceShape = findViewById(R.id.rv_faceShape);
        rvFaceShape.setHasFixedSize(true);
        ivReset = findViewById(R.id.iv_reset);
        tvReset = findViewById(R.id.tv_reset);
        line = findViewById(R.id.line);
        adapter.setItems(mData);

        adapter.register(FBFaceShape.class, new FBFaceShapeItemViewBinder());

        rvFaceShape.addItemDecoration(new
                MyItemDecoration(5)
        );

        rvFaceShape.setAdapter(adapter);

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
        adapter.notifyItemChanged(FBUICacheUtils.faceShapePosition());
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        //更新ui状态
        FBState.currentSecondViewState = FBViewState.FACE_SHAPE;
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
        Log.i("gao", "onBindViewHolder: "+FBUICacheUtils.faceShapeResetEnable());
        btnReset.setEnabled(FBUICacheUtils.faceShapeResetEnable());
        ivReset.setEnabled(FBUICacheUtils.faceShapeResetEnable());
        tvReset.setEnabled(FBUICacheUtils.faceShapeResetEnable());
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



}
