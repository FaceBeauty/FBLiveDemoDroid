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


import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBFilterEnum;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBFunnyFilterConfig;
import com.nimo.fb_effect.model.FBFunnyFilterEnum;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.FBUICacheUtils;

import java.util.Locale;

import me.drakeet.multitype.ItemViewBinder;

/**
 * 滤镜Item的适配器
 */
public class FBFunnyFilterItemViewBinder extends ItemViewBinder<FBFunnyFilterConfig.FBFunnyFilter,
        FBFunnyFilterItemViewBinder.ViewHolder> {

  @NonNull @Override protected FBFunnyFilterItemViewBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    View root = inflater.inflate(R.layout.item_drawable_top_button, parent, false);
    return new FBFunnyFilterItemViewBinder.ViewHolder(root);
  }

  @SuppressLint("SetTextI18n") @Override protected void
  onBindViewHolder(@NonNull FBFunnyFilterItemViewBinder.ViewHolder holder, @NonNull FBFunnyFilterConfig.FBFunnyFilter item) {

    //根据缓存中的选中的哪一个判断当前item是否被选中
    holder.itemView.setSelected(getPosition(holder) ==
        FBUICacheUtils.getFunnyFilterPosition());

    String currentLanguage = Locale.getDefault().getLanguage();
    if("en".equals(currentLanguage)){
      holder.text.setText(item.getTitleEn());
    }else{
      holder.text.setText(item.getTitle());
    }

    // holder.text.setBackgroundColor(Color.TRANSPARENT);
    //
    // holder.text.setTextColor(HtState.isDark ? Color.WHITE : ContextCompat
    //     .getColor(holder.itemView.getContext(),R.color.dark_black));

    if (FBState.isDark) {
      holder.image.setImageDrawable(FBFunnyFilterEnum.values()[getPosition(holder)].getIconResBlack(holder.itemView.getContext()));
      holder.text.setTextColor(
          ContextCompat.getColorStateList(holder.itemView.getContext(),
              R.color.color_selector_tab_dark));
    }else{
      holder.image.setImageDrawable(FBFunnyFilterEnum.values()[getPosition(holder)].getIconResWhite(holder.itemView.getContext()));
      holder.text.setTextColor(
          ContextCompat.getColorStateList(holder.itemView.getContext(),
              R.color.color_selector_tab_light));

    }

    // holder.maker.setBackgroundColor(ContextCompat.getColor
    //     (holder.itemView.getContext(), R.color.filter_maker));
    // holder.maker.setVisibility(
    //     holder.itemView.isSelected() ? View.VISIBLE : View.GONE
    // );

    // if(HtState.currentStyle != HtStyle.YUAN_TU){
    //   holder.itemView.setEnabled(false);
    //   RxBus.get().post(HTEventAction.ACTION_STYLE_SELECTED,"请先取消“风格推荐”效果");
    // }else{
    //   holder.itemView.setEnabled(true);
    //   RxBus.get().post(HTEventAction.ACTION_STYLE_SELECTED,"");
    // }
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (holder.itemView.isSelected()) {
          return;
        }

        //应用效果
        FBEffect.shareInstance().setFilter(FBFilterEnum.FBFilterFunny.getValue(),item.getName());
        //HtUICacheUtils.beautyFilterValue(item, 100);
        FBState.currentFunnyFilter = item;
        holder.itemView.setSelected(true);
        getAdapter().notifyItemChanged(FBUICacheUtils.getFunnyFilterPosition());
        FBUICacheUtils.setFunnyFilterPosition(getPosition(holder));
        FBUICacheUtils.setFunnyFilterName(item.getName());
        getAdapter().notifyItemChanged(FBUICacheUtils.getFunnyFilterPosition());

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
