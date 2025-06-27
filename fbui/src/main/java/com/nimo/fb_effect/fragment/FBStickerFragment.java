package com.nimo.fb_effect.fragment;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.adapter.FBStickerAdapter;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBStickerConfig;
import com.nimo.fb_effect.utils.FBConfigCallBack;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import java.util.ArrayList;
import java.util.List;

/**
 * AR道具——贴纸
 */
public class FBStickerFragment extends FBBaseLazyFragment {

  private final List<FBStickerConfig.FBSticker> items = new ArrayList<>();
  FBStickerAdapter stickerkAdapter;

  @Override protected int getLayoutId() {
    return R.layout.fragment_fb_sticker;
  }

  @Override protected void initView(View view, Bundle savedInstanceState) {
    if (getContext() == null) return;

    items.clear();
    // items.add(FBSticker.NO_STICKER);

    FBStickerConfig stickerList = FBConfigTools.getInstance().getStickerList();

    if (stickerList != null && stickerList.getStickers() != null && stickerList.getStickers().size() != 0) {
      items.addAll(stickerList.getStickers());
      // items.add(FBSticker.NEW_STICKER);
      initRecyclerView();
    } else {
      FBConfigTools.getInstance().getStickersConfig(new FBConfigCallBack<List<FBStickerConfig.FBSticker>>() {
        @Override public void success(List<FBStickerConfig.FBSticker> list) {
          items.addAll(list);
          // items.add(HtSticker.NEW_STICKER);
          initRecyclerView();
        }

        @Override public void fail(Exception error) {
          Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
      });
    }

  }

  public void initRecyclerView() {
    RecyclerView fbStickerRV = (RecyclerView) findViewById(R.id.fbRecyclerView);
    stickerkAdapter = new FBStickerAdapter(items);
//    htStickerRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
    fbStickerRV.setAdapter(stickerkAdapter);
  }

  /*@Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_SYNC_STICKER_ITEM_CHANGED) })
  public void changedPoint(Object o) {
    int lastposition = FBSelectedPosition.POSITION_STICKER;
    FBSelectedPosition.POSITION_STICKER = -1;
    stickerkAdapter.notifyItemChanged(lastposition);

  }*/
  @Subscribe(thread = EventThread.MAIN_THREAD,
          tags = { @Tag(FBEventAction.ACTION_SYNC_STICKER_ITEM_CHANGED) })
  public void changedPoint(Object o) {
    if (stickerkAdapter == null) {
      Log.e("FBStickerFragment", "stickerkAdapter is null, cannot notify item changed.");
      return;
    }

    int lastPosition = FBSelectedPosition.POSITION_STICKER;

    FBSelectedPosition.POSITION_STICKER = -1;

    if (lastPosition >= 0 && lastPosition < stickerkAdapter.getItemCount()) {
      stickerkAdapter.notifyItemChanged(lastPosition);
    } else {
      Log.w("FBStickerFragment", "Invalid position: " + lastPosition);
    }
  }




  @Override
  protected void onDestroyViewLazy() {
    super.onDestroyViewLazy();
  }
}
