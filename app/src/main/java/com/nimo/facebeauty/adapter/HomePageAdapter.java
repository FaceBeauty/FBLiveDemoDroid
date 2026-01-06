package com.nimo.facebeauty.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nimo.facebeauty.R;
import com.nimo.facebeauty.FBApplication;
//import com.nimo.facebeauty.activity.Camera2Activity;
import com.nimo.facebeauty.activity.CameraActivity;
import com.nimo.facebeauty.activity.MainActivity;
import com.nimo.facebeauty.activity.SampleCameraActivity;
import com.nimo.facebeauty.model.HomePageItem;
import com.nimo.facebeauty.tools.ToastUtils;
import java.util.ArrayList;
import java.util.List;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ViewHolder> {

  private final List<HomePageItem> items;

  private final Activity activity;

  public HomePageAdapter(@NonNull Activity activity) {
    this.items = new ArrayList<>();
    items.add(new HomePageItem("最简示例(无Camera模块)", SampleCameraActivity.class));
    items.add(new HomePageItem("Camera2自采集示例", CameraActivity.class));
    this.activity = activity;
  }

  @NonNull @Override public ViewHolder
  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_page, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    final HomePageItem item = items.get(position);
    holder.tvTitle.setText(item.getTitle());
    // 点击跳转
    holder.itemView.setOnClickListener(
        new View.OnClickListener() {
          @Override public void onClick(View v) {
            Intent intent = new Intent(activity, item.getActivity());
            holder.itemView.setTransitionName("shared_element_container");

            if (!FBApplication.hasInit) {
              ToastUtils.getInstance().toast("初始化失败了，" +
                  "请替换成您key对应的包名和应用名后重试");
            }

            if (!MainActivity.canUseCamera) {
              ToastUtils.getInstance().toast("请授予必要的相机权限");
            }
            if (FBApplication.hasInit && MainActivity.canUseCamera) {
              activity.startActivity(intent);
            }
          }
        }
    );
  }

  @Override public int getItemCount() {
    return items.size();
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {

    private final AppCompatTextView tvTitle;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvTitle = itemView.findViewById(R.id.tv_title);
    }
  }
}
