package com.nimo.fb_effect.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.adapter.GiftAdapter;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.GiftConfig;
import com.nimo.fb_effect.utils.FBConfigCallBack;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.fb_effect.utils.FBSelectedPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * AR道具——礼物
 */
public class GiftFragment extends FBBaseLazyFragment {

    private final List<GiftConfig.FbGift> items = new ArrayList<>();
    GiftAdapter giftAdapter;


    @Override protected int getLayoutId() {
        return R.layout.fragment_fb_sticker;
    }

    @Override protected void initView(View view, Bundle savedInstanceState) {
        if (getContext() == null) return;

        items.clear();
        // items.add(FBMask.NO_MASK);

        GiftConfig giftList = FBConfigTools.getInstance().getGiftList();

        if (giftList != null && giftList.getGifts() != null && giftList.getGifts().size() != 0) {
            items.addAll(giftList.getGifts());
            initRecyclerView();
        } else {
            FBConfigTools.getInstance().getGiftsConfig(new FBConfigCallBack<List<GiftConfig.FbGift>>() {
                @Override public void success(List<GiftConfig.FbGift> list) {
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
        RecyclerView fbGiftRV = (RecyclerView) findViewById(R.id.fbRecyclerView);
        giftAdapter = new GiftAdapter(items);
//        fbMaskRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        fbGiftRV.setAdapter(giftAdapter);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
               tags = { @Tag(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED) })
    public void changedPoint(Object o) {
        int lastposition = FBSelectedPosition.POSITION_GIFT;
        FBSelectedPosition.POSITION_MASK = -1;
        giftAdapter.notifyItemChanged(lastposition);

    }

//    @SuppressLint("NotifyDataSetChanged")
//    @Override protected void onFragmentStartLazy() {
//        super.onFragmentStartLazy();
//        //同步item改变
//        RxBus.get().post(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED, "");
//    }

//    @Subscribe(thread = EventThread.MAIN_THREAD,
//            tags = { @Tag(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED) })
//    public void changedPoint(Object o) {
//        if (giftAdapter == null) {
//            Log.e("FBStickerFragment", "giftAdapter is null, cannot notify item changed.");
//            return;
//        }
//
//        int lastPosition = FBSelectedPosition.POSITION_MASK;
//
//        FBSelectedPosition.POSITION_MASK = -1;
//
//        if (lastPosition >= 0 && lastPosition < giftAdapter.getItemCount()) {
//            giftAdapter.notifyItemChanged(lastPosition);
//        } else {
//            Log.w("FBStickerFragment", "Invalid position: " + lastPosition);
//        }
//    }

}
