package com.nimo.fb_effect.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.hwangjr.rxbus.RxBus;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBFilterEnum;
import com.nimo.facebeauty.model.FBItemEnum;
import java.lang.ref.WeakReference;

/**
 * 重置Dialog
 */
public class FBResetAllDialog extends DialogFragment {

  private View root;
  private Context context;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    context = new WeakReference<>(getActivity()).get();
    root = LayoutInflater.from(context).inflate(R.layout.dialog_reset_all, null);
    Dialog dialog = new Dialog(context, R.style.TiDialog);
    dialog.setContentView(root);
    dialog.setCancelable(true);
    dialog.setCanceledOnTouchOutside(true);

    Window window = dialog.getWindow();
    if (window != null) {
      WindowManager.LayoutParams params = window.getAttributes();
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      params.height = WindowManager.LayoutParams.WRAP_CONTENT;
      params.gravity = Gravity.CENTER;
      window.setAttributes(params);
    }

    return dialog;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    root.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

          FBUICacheUtils.resetSkinValue(getContext());
          FBUICacheUtils.beautySkinResetEnable(false);

          FBUICacheUtils.resetFaceTrimValue(getContext());
          FBUICacheUtils.beautyFaceTrimResetEnable(false);


          FBSelectedPosition.POSITION_STICKER = -1;
          FBEffect.shareInstance().setARItem(FBItemEnum.FBItemSticker.getValue(), "");
          RxBus.get().post(FBEventAction.ACTION_SYNC_STICKER_ITEM_CHANGED, "");

          FBSelectedPosition.POSITION_MASK = -1;
          FBEffect.shareInstance().setARItem(FBItemEnum.FBItemMask.getValue(), "");
          RxBus.get().post(FBEventAction.ACTION_SYNC_MASK_ITEM_CHANGED, "");

          FBSelectedPosition.POSITION_GIFT = -1;
          FBEffect.shareInstance().setARItem(FBItemEnum.FBItemGift.getValue(), "");
          RxBus.get().post(FBEventAction.ACTION_SYNC_GIFT_ITEM_CHANGED, "");

          FBSelectedPosition.POSITION_WATERMARK = -1;
          FBEffect.shareInstance().setARItem(FBItemEnum.FBItemWatermark.getValue(), "");
          RxBus.get().post(FBEventAction.ACTION_SYNC_WATERMARK_ITEM_CHANGED, "");

          FBSelectedPosition.POSITION_AISEGMENTATION = -1;
          FBEffect.shareInstance().setAISegEffect("");
          RxBus.get().post(FBEventAction.ACTION_SYNC_PORTRAITAI_ITEM_CHANGED, "");

          FBSelectedPosition.POSITION_GREEN_SCREEN = -1;
          FBEffect.shareInstance().setChromaKeyingScene("");
          RxBus.get().post(FBEventAction.ACTION_SYNC_PORTRAITTGS_ITEM_CHANGED, "");

          FBSelectedPosition.POSITION_GESTURE = -1;
          FBEffect.shareInstance().setGestureEffect("");
          RxBus.get().post(FBEventAction.ACTION_SYNC_GESTURE_ITEM_CHANGED, "");

          FBEffect.shareInstance().setFilter(FBFilterEnum.FBFilterBeauty.getValue(), "");
          FBUICacheUtils.setBeautyFilterPosition(0);

          FBEffect.shareInstance().setARItem(FBItemEnum.FBItemAvatar.getValue(),"");
          FBSelectedPosition.POSITION_THREED = -1;
          RxBus.get().post(FBEventAction.ACTION_SYNC_THREED_ITEM_CHANGED, "");


          //通知刷新列表
          RxBus.get().post(FBEventAction.ACTION_SYNC_RESET, "true");

          //通知更新滑动条显示状态
          RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");


        dismiss();
      }
    });

    root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
  }

}
