package com.nimo.fb_effect.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import androidx.annotation.NonNull;;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.bumptech.glide.Glide;
import com.hwangjr.rxbus.RxBus;
import com.nimo.facebeauty.FBEffect;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.FBBeautyFilterConfig;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.view.FBRoundImageView;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBFilterEnum;
import com.nimo.facebeauty.model.FBFilterEnum;
import java.util.Locale;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 滤镜Item的适配器
 */
public class FBBeautyFilterItemViewBinder extends ItemViewBinder<FBBeautyFilterConfig.FBBeautyFilter,
    FBBeautyFilterItemViewBinder.ViewHolder> {

  @NonNull @Override protected FBBeautyFilterItemViewBinder.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    View root = inflater.inflate(R.layout.item_filter, parent, false);
    return new FBBeautyFilterItemViewBinder.ViewHolder(root);
  }

  @SuppressLint("SetTextI18n") @Override protected void
  onBindViewHolder(@NonNull FBBeautyFilterItemViewBinder.ViewHolder holder, @NonNull FBBeautyFilterConfig.FBBeautyFilter item) {

    //根据缓存中的选中的哪一个判断当前item是否被选中
    holder.itemView.setSelected(getPosition(holder) ==
        FBUICacheUtils.getBeautyFilterPosition());

    String currentLanguage = Locale.getDefault().getLanguage();
    if("en".equals(currentLanguage)){
      holder.name.setText(item.getTitleEn());
    }else{
      holder.name.setText(item.getTitle());
    }

    holder.name.setBackgroundColor(Color.TRANSPARENT);

    holder.name.setTextColor(FBState.isDark ? Color.WHITE : ContextCompat
        .getColor(holder.itemView.getContext(),R.color.dark_black));

    // holder.thumbIV.setClipToOutline(true);
    // GradientDrawable drawable = new GradientDrawable();
    // drawable.setCornerRadius(10);
    // holder.thumbIV.setImageDrawable(drawable);

    String resName = "ic_filter_" + item.getName();
    Log.d("resName", "styleName: " + resName);
    int resID = holder.itemView.getResources().getIdentifier(resName, "drawable",
        holder.itemView.getContext().getPackageName());
    Log.d("resName", "resId: " + resID);
    Glide.with(holder.itemView.getContext())
        .load(resID)
        // .placeholder(R.drawable.icon_placeholder)
        .into(holder.thumbIV);

    holder.maker.setVisibility(
        holder.itemView.isSelected() ? View.VISIBLE : View.GONE
    );

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        if(true){
          if (holder.itemView.isSelected()) {
            return;
          }


          //应用效果


          FBEffect.shareInstance().setFilter(FBFilterEnum.FBFilterBeauty.getValue(), item.getName());

          FBState.currentStyleFilter = item;

          holder.itemView.setSelected(true);
          getAdapter().notifyItemChanged(FBUICacheUtils.getBeautyFilterPosition());
          FBUICacheUtils.setBeautyFilterPosition(getPosition(holder));
          FBUICacheUtils.setBeautyFilterName(item.getName());
          getAdapter().notifyItemChanged(FBUICacheUtils.getBeautyFilterPosition());


          RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");
          RxBus.get().post(FBEventAction.ACTION_SHOW_FILTER, "");
        }else{
          RxBus.get().post(FBEventAction.ACTION_MAKEUP_STYLE_SELECTED, "");
        }

      }
    });

  }

  static class ViewHolder extends RecyclerView.ViewHolder {

    public final @NonNull AppCompatTextView name;

    public final @NonNull FBRoundImageView thumbIV;

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
