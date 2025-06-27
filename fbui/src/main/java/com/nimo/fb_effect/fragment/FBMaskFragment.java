package com.nimo.fb_effect.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.adapter.FBMaskAdapter;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBMaskConfig;
import com.nimo.fb_effect.model.FBMaskConfig.FBMask;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.utils.FBConfigCallBack;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import java.util.ArrayList;
import java.util.List;

/**
 * AR道具——面具
 */
public class FBMaskFragment extends FBBaseLazyFragment {

    private final List<FBMask> items = new ArrayList<>();
    FBMaskAdapter maskAdapter;


    @Override protected int getLayoutId() {
        return R.layout.fragment_fb_sticker;
    }

    @Override protected void initView(View view, Bundle savedInstanceState) {
        if (getContext() == null) return;

        items.clear();
        // items.add(FBMask.NO_MASK);

        FBMaskConfig maskList = FBConfigTools.getInstance().getMaskList();

        if (maskList != null && maskList.getMasks() != null && maskList.getMasks().size() != 0) {
            items.addAll(maskList.getMasks());
            initRecyclerView();
        } else {
            FBConfigTools.getInstance().getMasksConfig(new FBConfigCallBack<List<FBMask>>() {
                @Override public void success(List<FBMask> list) {
                    items.addAll(list);
                    initRecyclerView();
                }

                @Override public void fail(Exception error) {
                    Looper.prepare();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            });
        }

    }

    private void initRecyclerView() {
        RecyclerView fbMaskRV = (RecyclerView) findViewById(R.id.fbRecyclerView);
        maskAdapter = new FBMaskAdapter(items);
//        fbMaskRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        fbMaskRV.setAdapter(maskAdapter);
    }

    /*@Subscribe(thread = EventThread.MAIN_THREAD,
               tags = { @Tag(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED) })
    public void changedPoint(Object o) {
        int lastposition = FBSelectedPosition.POSITION_MASK;
        FBSelectedPosition.POSITION_MASK = -1;
        maskAdapter.notifyItemChanged(lastposition);

    }*/

//    @SuppressLint("NotifyDataSetChanged")
//    @Override protected void onFragmentStartLazy() {
//        super.onFragmentStartLazy();
//        //同步item改变
//        RxBus.get().post(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED, "");
//    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = { @Tag(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED) })
    public void changedPoint(Object o) {
        if (maskAdapter == null) {
            Log.e("FBStickerFragment", "maskAdapter is null, cannot notify item changed.");
            return;
        }

        int lastPosition = FBSelectedPosition.POSITION_MASK;

        FBSelectedPosition.POSITION_MASK = -1;

        if (lastPosition >= 0 && lastPosition < maskAdapter.getItemCount()) {
            maskAdapter.notifyItemChanged(lastPosition);
        } else {
            Log.w("FBStickerFragment", "Invalid position: " + lastPosition);
        }
    }

}
