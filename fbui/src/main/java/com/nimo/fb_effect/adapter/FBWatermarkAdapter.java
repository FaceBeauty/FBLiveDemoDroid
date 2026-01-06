package com.nimo.fb_effect.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hwangjr.rxbus.RxBus;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.fragment.FBWatermarkFragment;
import com.nimo.fb_effect.model.FBDownloadState;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBWatermarkConfig;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import com.nimo.fb_effect.utils.FileUtils;


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 水印的Item适配器
 */
public class FBWatermarkAdapter extends RecyclerView.Adapter<FBStickerViewHolder> {

    private final int ITEM_TYPE_ONE = 1;
    private final int ITEM_TYPE_TWO = 2;
    private final String TAG = "watermark";
    Bitmap bitmap;

    private int selectedPosition = FBSelectedPosition.POSITION_WATERMARK;
    private int lastPosition;
    private int deletePosition = -1;

    private final List<FBWatermarkConfig.FBWatermark> watermarkList;
    private final FBWatermarkFragment.waterMarkClick watermarkClick;

    private final Map<String, String> downloadingWatermarks = new ConcurrentHashMap<>();

    public FBWatermarkAdapter(List<FBWatermarkConfig.FBWatermark> stickerList, FBWatermarkFragment.waterMarkClick click) {
        this.watermarkList = stickerList;
        watermarkClick = click;
        DownloadDispatcher.setMaxParallelRunningCount(5);
        RxBus.get().register(this);
    }

    @Override
    public int getItemViewType(int position) {
        // if (position == 0) {
        //     return ITEM_TYPE_ONE;
        // } else {
            return ITEM_TYPE_TWO;
        // }
    }

    @NonNull
    @Override
    public FBStickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // if (viewType == ITEM_TYPE_ONE) {
        //     view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_FB_sticker_one, parent, false);
        // } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fb_sticker, parent, false);
        // }
        return new FBStickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FBStickerViewHolder holder, int position) {

        final FBWatermarkConfig.FBWatermark FBWatermark = watermarkList.get(holder.getAdapterPosition());
        selectedPosition = FBSelectedPosition.POSITION_WATERMARK;
        holder.deleteIV.setVisibility(View.GONE);



        if (selectedPosition == position) {
            holder.itemView.setSelected(true);

        } else {
            holder.itemView.setSelected(false);
        }

        //显示封面
        if (FBWatermark == FBWatermarkConfig.FBWatermark.NO_WATERMARK) {
            holder.thumbIV.setImageResource(R.drawable.resource_shangchuan);
        } else {
            if(position == 0){
                Glide.with(holder.itemView.getContext())
                    .load(R.drawable.resource_shangchuan)
                    .into(holder.thumbIV);
            } else if (FBWatermark.isFromDisk()) {
                //来自硬盘的直接加载本体图片
                Glide.with(holder.itemView.getContext())
                    .load(new File(FBWatermark.getDir()))
                    .placeholder(R.drawable.icon_placeholder)
                    .into(holder.thumbIV);

            }else{
                Glide.with(holder.itemView.getContext())
                    .load(watermarkList.get(position).getIcon())
                    .placeholder(R.drawable.icon_placeholder)
                    .into(holder.thumbIV);
            }

        }

        FBWatermark.setDownloaded(FBDownloadState.COMPLETE_DOWNLOAD);
        //判断是否已经下载
        if (FBWatermark.isDownloaded() == FBDownloadState.COMPLETE_DOWNLOAD) {
            holder.downloadIV.setVisibility(View.GONE);
            holder.loadingIV.setVisibility(View.GONE);
            holder.loadingBG.setVisibility(View.GONE);
            holder.stopLoadingAnimation();
        } else {
            //判断是否正在下载，如果正在下载，则显示加载动画
            if (downloadingWatermarks.containsKey(FBWatermark.getName())) {
                holder.downloadIV.setVisibility(View.GONE);
                holder.loadingIV.setVisibility(View.VISIBLE);
                holder.loadingBG.setVisibility(View.VISIBLE);
                holder.startLoadingAnimation();
            } else {
                holder.downloadIV.setVisibility(View.VISIBLE);
                holder.loadingIV.setVisibility(View.GONE);
                holder.loadingBG.setVisibility(View.GONE);
                holder.stopLoadingAnimation();
            }
        }

            holder.deleteIV.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View v) {
                    if (FBWatermark.isFromDisk()) {
                        if(holder.getAdapterPosition() == selectedPosition){
                            //如果点击已选中的效果，则取消效果
                            FBEffect.shareInstance().setARItem(FBItemEnum.FBItemWatermark.getValue(), "");
                            RxBus.get().post(FBEventAction.ACTION_REMOVE_STICKER_RECT,"");
                            FBSelectedPosition.POSITION_WATERMARK = -1;
                            notifyItemChanged(selectedPosition);
                        }
                        FBWatermark.delete(deletePosition);
                        watermarkClick.deleteWaterMarkFromDisk(deletePosition);
                        holder.itemView.setClickable(false);
                        FileUtils.deleteDirOrFile(FBWatermark.getDir());
                        String path = FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemWatermark.getValue()) + "/" + FBWatermark.getName();
                        FileUtils.deleteDirOrFile(path);

                    }

                }
            });

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FBWatermark.isFromDisk()) {
                        if(holder.getAdapterPosition() == selectedPosition){
                            //如果点击已选中的效果，则取消效果
                            FBEffect.shareInstance().setARItem(FBItemEnum.FBItemWatermark.getValue(), "");
                            RxBus.get().post(FBEventAction.ACTION_REMOVE_STICKER_RECT,"");
                            FBSelectedPosition.POSITION_WATERMARK = -1;
                            notifyItemChanged(selectedPosition);
                        }else{
                            FBEffect.shareInstance().setARItem(FBItemEnum.FBItemWatermark.getValue(), FBWatermark.getName());

                            bitmap = BitmapFactory.decodeFile(FBWatermark.getDir());
                            RxBus.get().post(FBEventAction.ACTION_REMOVE_STICKER_RECT, "");
                            RxBus.get().post(FBEventAction.ACTION_ADD_STICKER_RECT, bitmap);

                            int lastPosition = selectedPosition;
                            selectedPosition = holder.getAdapterPosition();
                            FBSelectedPosition.POSITION_WATERMARK = selectedPosition;
                            notifyItemChanged(selectedPosition);
                            notifyItemChanged(lastPosition);
                        }

                    }else{
                        if(holder.getAdapterPosition() == 0){
                            watermarkClick.clickWaterMarkFromDisk();
                            return;

                        }else{
                            //如果没有下载，则开始下载到本地
                            if (FBWatermark.isDownloaded() == FBDownloadState.NOT_DOWNLOAD) {

                                int currentPosition = holder.getAdapterPosition();

                                //如果已经在下载了，则不操作
                                if (downloadingWatermarks.containsKey(FBWatermark.getName())) {
                                    return;
                                }
                                // new DownloadTask.Builder(FBWatermark.getUrl(), new File(FBEffect.shareInstance().getWatermarkPath()))
                                //         .setMinIntervalMillisCallbackProcess(30)
                                //         .build()
                                //         .enqueue(new DownloadListener2() {
                                //             @Override
                                //             public void taskStart(@NonNull DownloadTask task) {
                                //                 downloadingWatermarks.put(FBWatermark.getName(), FBWatermark.getUrl());
                                //                 handler.post(new Runnable() {
                                //                     @Override
                                //                     public void run() {
                                //                         notifyDataSetChanged();
                                //                     }
                                //                 });
                                //             }
                                //
                                //             @Override
                                //             public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable final Exception realCause) {
                                //                 if (cause == EndCause.COMPLETED) {
                                //                     downloadingWatermarks.remove(FBWatermark.getName());
                                //
                                //                     //修改内存与文件
                                //                     FBWatermark.setDownloaded(FBDownloadState.COMPLETE_DOWNLOAD);
                                //                     FBWatermark.downloaded();
                                //
                                //                     handler.post(new Runnable() {
                                //                         @Override
                                //                         public void run() {
                                //                             notifyDataSetChanged();
                                //                         }
                                //                     });
                                //
                                //                 } else {
                                //                     downloadingWatermarks.remove(FBWatermark.getName());
                                //
                                //                     handler.post(new Runnable() {
                                //                         @Override
                                //                         public void run() {
                                //                             notifyDataSetChanged();
                                //                             if (realCause != null) {
                                //                                 Toast.makeText(holder.itemView.getContext(), realCause.getMessage(), Toast.LENGTH_SHORT).show();
                                //                             }
                                //                         }
                                //                     });
                                //                 }
                                //             }
                                //         });
                            } else {
                                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                                    return;
                                }

                                //如果已经下载了，则让水印生效
                                if (FBWatermark == FBWatermarkConfig.FBWatermark.NO_WATERMARK) {
                                    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemWatermark.getValue(), "");
                                    RxBus.get().post(FBEventAction.ACTION_REMOVE_STICKER_RECT,"");
                                    //切换选中背景
                                    int lastPosition = selectedPosition;
                                    selectedPosition = holder.getAdapterPosition();
                                    FBSelectedPosition.POSITION_WATERMARK = selectedPosition;
                                    notifyItemChanged(selectedPosition);
                                    notifyItemChanged(lastPosition);
                                }else if (holder.getAdapterPosition() == selectedPosition){
                                    //如果点击已选中的效果，则取消效果
                                    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemWatermark.getValue(), "");
                                    RxBus.get().post(FBEventAction.ACTION_REMOVE_STICKER_RECT,"");
                                    FBSelectedPosition.POSITION_WATERMARK = -1;
                                    notifyItemChanged(selectedPosition);
                                    // notifyItemChanged(-1);
                                } else {
                                    //todo 水印
                                    Log.d(TAG, "onClick: "+FBWatermark.getName());

                                    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemWatermark.getValue(), FBWatermark.getName());
                                    // FBEffect.shareInstance().setWatermarkParam();
                                    bitmap = BitmapFactory.decodeFile(FBWatermark.getIcon());
                                    RxBus.get().post(FBEventAction.ACTION_REMOVE_STICKER_RECT,"");
                                    RxBus.get().post(FBEventAction.ACTION_ADD_STICKER_RECT,bitmap);

                                    //切换选中背景
                                    int lastPosition = selectedPosition;
                                    selectedPosition = holder.getAdapterPosition();
                                    FBSelectedPosition.POSITION_WATERMARK = selectedPosition;
                                    notifyItemChanged(selectedPosition);
                                    notifyItemChanged(lastPosition);
                                }

                            }
                        }
                    }
                    if(deletePosition != -1){
                        notifyItemChanged(deletePosition);
                        deletePosition = -1;
                    }
                    RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");

                }
            });
            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    Log.i(TAG, "onLongClick: long========");
                    if (FBWatermark.isFromDisk()) {
                        holder.deleteIV.setVisibility(View.VISIBLE);
                        deletePosition = holder.getAdapterPosition();
                    }
                    return true;
                }
            });




    }

    @Override
    public int getItemCount() {
        return watermarkList == null ? 0 : watermarkList.size();
    }

    public void selectItem(int selectedPosition) {
        int lastPosition = selectedPosition;
        this.selectedPosition = selectedPosition;
        FBSelectedPosition.POSITION_WATERMARK = selectedPosition;
        FBEffect.shareInstance().setARItem(FBItemEnum.FBItemWatermark.getValue(), watermarkList.get(selectedPosition).getName());
        notifyItemChanged(selectedPosition);
        notifyItemChanged(lastPosition);
    }
}
