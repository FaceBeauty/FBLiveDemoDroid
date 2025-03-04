package com.nimo.fb_effect.adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hwangjr.rxbus.RxBus;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBFaceTrim;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 美型的Item适配器
 */
public class FBFaceTrimItemViewBinder
    extends ItemViewBinder<FBFaceTrim, FBFaceTrimItemViewBinder.ViewHolder> {

  @NonNull @Override protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    View root = inflater.inflate(R.layout.item_drawable_top_button, parent, false);
    return new ViewHolder(root);
  }

  @SuppressLint("SetTextI18n")
  @Override protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull FBFaceTrim item) {

    //-50到50参数区间 ui逻辑为50 为初始值
    String key = item.name();

    int initialValue = 0;
    if (key.equals(FBFaceTrim.CHIN_TRIMMING.name()) ||
        key.equals(FBFaceTrim.FOREHEAD_TRIM.name()) ||
        key.equals(FBFaceTrim.EYE_SAPCING.name()) ||
        key.equals(FBFaceTrim.EYE_CORNER_TRIMMING.name()) ||
        key.equals(FBFaceTrim.NOSE_ENLARGING.name()) ||
        key.equals(FBFaceTrim.PHILTRUM_TRIMMING.name()) ||
        key.equals(FBFaceTrim.MOUTH_TRIMMING.name())
    ) {
      initialValue = 50;
    }

    holder.itemView.setSelected(getPosition(holder) ==
        FBUICacheUtils.beautyFaceTrimPosition());

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

    //获取当前的item对应的参数 如果不是0 则表示当前的item是变动的 加上蓝点提示
    holder.point.setVisibility((FBUICacheUtils
        .beautyFaceTrimValue(item) != initialValue) ?
                               View.VISIBLE : View.INVISIBLE);

    //同步滑动条
     RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        holder.itemView.setSelected(true);
        getAdapter().notifyItemChanged(FBUICacheUtils.beautyFaceTrimPosition());
        FBUICacheUtils.beautyFaceTrimPosition(getPosition(holder));
        getAdapter().notifyItemChanged(FBUICacheUtils.beautyFaceTrimPosition());
        FBState.currentFaceTrim = item;
        //同步滑动条
        RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");
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
