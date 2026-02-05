package com.nimo.fb_effect.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hwangjr.rxbus.RxBus;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBFaceShapeValue;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.MakeUpEnum;
import com.nimo.fb_effect.utils.FBUICacheUtils;

import java.util.List;

import me.drakeet.multitype.ItemViewBinder;

/**
 * 美妆的Item适配器
 */
public class MakeUpViewBinder
    extends ItemViewBinder<MakeUpEnum, MakeUpViewBinder.ViewHolder> {

  @NonNull @Override protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    View root = inflater.inflate(R.layout.item_drawable_top_button, parent, false);
    return new ViewHolder(root);
  }

  @SuppressLint("SetTextI18n")
  @Override protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MakeUpEnum item) {


//    String key = item.name();
//    List<MakeUpEnum> makeUpList;
//
//    int initValue = 0;


    holder.itemView.setSelected(getPosition(holder) ==
        FBUICacheUtils.getBeautyMakeUpPosition());

    holder.text.setText(item.getName(holder.itemView.getContext()));

    //根据屏幕是否占满显示不同的图标
    if (FBState.isDark) {
      holder.image.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),
          item.getDrawableRes_white()));

      holder.text.setTextColor(
          ContextCompat.getColorStateList(holder.itemView.getContext(),
              R.color.color_selector_tab_dark));
    } else {
      holder.image.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),
          item.getDrawableRes_black()));
      holder.text.setTextColor(
          ContextCompat.getColorStateList(holder.itemView.getContext(),
              R.color.color_selector_tab_light));
    }
    ;
//获取当前的item对应的参数 如果不是0 则表示当前的item是变动的 加上蓝点提示
      holder.point.setVisibility(getPosition(holder) ==
              FBUICacheUtils.getBeautyMakeUpPosition() ?
              View.VISIBLE : View.INVISIBLE);


    //同步滑动条
     RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
          Log.i("gao", "onClick: "+item.name());
          holder.itemView.setSelected(true);
          getAdapter().notifyItemChanged(FBUICacheUtils.getBeautyMakeUpPosition());
          FBUICacheUtils.setBeautyMakeupPosition(getPosition(holder));
          FBState.currentMakeUp = item;

          getAdapter().notifyItemChanged(FBUICacheUtils.getBeautyMakeUpPosition());

          switch (getPosition(holder)){
            case 0:
              RxBus.get().post(FBEventAction.ACTION_CHANGE_PANEL, FBViewState.MAKEUP_LIPSTICK);
              break;
            case 1:
              RxBus.get().post(FBEventAction.ACTION_CHANGE_PANEL, FBViewState.MAKEUP_EYEBROW);
              break;
            case 2:
              RxBus.get().post(FBEventAction.ACTION_CHANGE_PANEL, FBViewState.MAKEUP_BLUSH);
              break;
            case 3:
              RxBus.get().post(FBEventAction.ACTION_CHANGE_PANEL, FBViewState.MAKEUP_EYESHADOW);
              break;
            case 4:
              RxBus.get().post(FBEventAction.ACTION_CHANGE_PANEL, FBViewState.MAKEUP_EYELINE);
              break;
            case 5:
              RxBus.get().post(FBEventAction.ACTION_CHANGE_PANEL, FBViewState.MAKEUP_EYELASH);
              break;
            case 6:
              RxBus.get().post(FBEventAction.ACTION_CHANGE_PANEL, FBViewState.MAKEUP_BEAUTYPUPILS);
              break;

          }

      }
    });

  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    private final @NonNull AppCompatTextView text;

    private final @NonNull AppCompatImageView image;

    private final @NonNull View point;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      text = itemView.findViewById(R.id.fbTextTV);
      image = itemView.findViewById(R.id.fbImageIV);
      point = itemView.findViewById(R.id.point);
    }
  }

}
