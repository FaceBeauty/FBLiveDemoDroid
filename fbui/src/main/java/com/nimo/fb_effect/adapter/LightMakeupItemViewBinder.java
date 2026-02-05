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
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.LightMakeupConfig;
import com.nimo.fb_effect.utils.FBUICacheUtils;


import java.util.Locale;

import me.drakeet.multitype.ItemViewBinder;

/**
 * 美发Item的适配器
 */
public class LightMakeupItemViewBinder extends ItemViewBinder<LightMakeupConfig.LightMakeup,
        LightMakeupItemViewBinder.ViewHolder> {

    @NonNull @Override protected LightMakeupItemViewBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_filter, parent, false);
        return new LightMakeupItemViewBinder.ViewHolder(root);
    }


    @SuppressLint("SetTextI18n") @Override protected void
    onBindViewHolder(@NonNull LightMakeupItemViewBinder.ViewHolder holder, @NonNull LightMakeupConfig.LightMakeup item) {
        //根据缓存中的选中的哪一个判断当前item是否被选中
        holder.itemView.setSelected(getPosition(holder) ==
                FBUICacheUtils.getLightMakeupPosition());

        String currentLanguage = Locale.getDefault().getLanguage();
        if("en".equals(currentLanguage)){
            holder.name.setText(item.getTitleEn());
        }else{
            holder.name.setText(item.getTitle());
        }

        holder.name.setBackgroundColor(Color.TRANSPARENT);

        holder.name.setTextColor(FBState.isDark ? Color.WHITE : ContextCompat
                .getColor(holder.itemView.getContext(),R.color.dark_black));
        String resName;
        if (item.getName().isEmpty()) {
            resName = "ic_style_none";
        } else {
            resName = "ic_style_" + item.getName();
        }
        int resID = holder.itemView.getResources().getIdentifier(resName, "drawable",
                holder.itemView.getContext().getPackageName());
//        holder.thumbIV.setImageDrawable(FBHairEnum.values()[getPosition(holder)].getIcon(holder.itemView.getContext()));
        Glide.with(holder.itemView.getContext())
                .load(resID)
                // .placeholder(R.drawable.icon_placeholder)
                .into(holder.thumbIV);
        // holder.maker.setBackgroundColor(ContextCompat.getColor
        //     (holder.itemView.getContext(), R.color.filter_maker));

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
                FBEffect.shareInstance().setStyle(item.getName(),FBUICacheUtils.setLightMakeupValue(item.getName()));
                FBState.currentLightMakeup = item;

                holder.itemView.setSelected(true);
                getAdapter().notifyItemChanged(FBUICacheUtils.getLightMakeupPosition());
//                getAdapter().notifyItemChanged(FBUICacheUtils.getBeautyHairPosition());
                FBUICacheUtils.setLightMakeupPosition(getPosition(holder));
//                FBUICacheUtils.setBeautyHairPosition(getPosition(holder));
                FBUICacheUtils.setLightMakeupName(item.getName());
                getAdapter().notifyItemChanged(FBUICacheUtils.getLightMakeupPosition());


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

