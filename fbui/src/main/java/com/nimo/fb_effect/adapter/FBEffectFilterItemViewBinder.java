package com.nimo.fb_effect.adapter;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hwangjr.rxbus.RxBus;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBFilterEnum;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBEffectFilterConfig;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.FBUICacheUtils;

import java.util.Locale;

import me.drakeet.multitype.ItemViewBinder;

/**
 * 滤镜Item的适配器
 */
public class FBEffectFilterItemViewBinder extends ItemViewBinder<FBEffectFilterConfig.FBEffectFilter,
    FBEffectFilterItemViewBinder.ViewHolder> {

  @NonNull @Override protected FBEffectFilterItemViewBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    View root = inflater.inflate(R.layout.item_filter, parent, false);
    return new FBEffectFilterItemViewBinder.ViewHolder(root);
  }

  @SuppressLint("SetTextI18n") @Override protected void
  onBindViewHolder(@NonNull FBEffectFilterItemViewBinder.ViewHolder holder, @NonNull FBEffectFilterConfig.FBEffectFilter item) {

    //根据缓存中的选中的哪一个判断当前item是否被选中
    holder.itemView.setSelected(getPosition(holder) ==
        FBUICacheUtils.getEffectFilterPosition());

    String currentLanguage = Locale.getDefault().getLanguage();
    if("en".equals(currentLanguage)){
      holder.name.setText(item.getTitleEn());
    }else{
      holder.name.setText(item.getTitle());
    }

    holder.name.setBackgroundColor(Color.TRANSPARENT);

    holder.name.setTextColor(FBState.isDark ? Color.WHITE : ContextCompat
        .getColor(holder.itemView.getContext(),R.color.dark_black));

    String resName = item.getIcon().substring(0, item.getIcon().indexOf("."));
    int resID = holder.itemView.getResources().getIdentifier(resName, "drawable",
        holder.itemView.getContext().getPackageName());
    Glide.with(holder.itemView.getContext())
        .load(resID)
        // .placeholder(R.drawable.icon_placeholder)
        .into(holder.thumbIV);

    // holder.maker.setBackgroundColor(ContextCompat.getColor
    //     (holder.itemView.getContext(), R.color.makeup_maker));
    holder.maker.setVisibility(
        holder.itemView.isSelected() ? View.VISIBLE : View.GONE
    );

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


        FBEffect.shareInstance().setFilter(FBFilterEnum.FBFilterEffect.getValue(),item.getName());
        //HtUICacheUtils.beautyFilterValue(item, 100);

        FBState.currentEffectFilter = item;

        holder.itemView.setSelected(true);
        getAdapter().notifyItemChanged(FBUICacheUtils.getEffectFilterPosition());
        FBUICacheUtils.setEffectFilterPosition(getPosition(holder));
        FBUICacheUtils.setEffectFilterName(item.getName());
//        Log.d("texiao", "onBindViewHolder:------ "+FBUICacheUtils.getEffectFilterName());
        getAdapter().notifyItemChanged(FBUICacheUtils.getEffectFilterPosition());


        RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");
        // RxBus.get().post(HTEventAction.ACTION_SHOW_FILTER, "");
      }
    });

  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    public final @NonNull AppCompatTextView name;

    public final @NonNull AppCompatImageView thumbIV;

    public final @NonNull AppCompatImageView maker;

    public final @NonNull AppCompatImageView loadingIV;

    public final @NonNull AppCompatImageView downloadIV;



    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      name = itemView.findViewById(R.id.tv_name);
      thumbIV = itemView.findViewById(R.id.iv_icon);
      maker = itemView.findViewById(R.id.bg_maker);
      loadingIV = itemView.findViewById(R.id.loadingIV);
      downloadIV = itemView.findViewById(R.id.downloadIV);
    }
    public void startLoadingAnimation() {
      Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.loading_animation);
      loadingIV.startAnimation(animation);
    }

    public void stopLoadingAnimation() {
      loadingIV.clearAnimation();
    }
  }

}
