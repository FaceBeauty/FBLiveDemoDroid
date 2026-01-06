package com.nimo.fb_effect.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.adapter.FBWatermarkAdapter;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.FBDownloadState;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBWatermarkConfig;
import com.nimo.fb_effect.utils.FBConfigCallBack;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.utils.FBUploadBitmapUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * AR道具——水印
 */
public class FBWatermarkFragment extends FBBaseLazyFragment {
    private FBWatermarkAdapter watermarkAdapter;

    private final List<FBWatermarkConfig.FBWatermark> items = new ArrayList<>();

    private static final int IMAGE_REQUEST_CODE = 0;//标题图片的选中返回

    @Override protected int getLayoutId() {
        return R.layout.fragment_fb_sticker;
    }

    @Override protected void initView(View view, Bundle savedInstanceState) {
        if (getContext() == null) return;

        items.clear();
        // items.add(FBWatermarkConfig.FBWatermark.NO_WATERMARK);

        FBWatermarkConfig watermarkList = FBConfigTools.getInstance().getWatermarkList();
        FBState.currentViewState = FBViewState.AR_WATERMARK;
//        RxBus.get().post(FBEventAction.ACTION_SYNC_WATERMARK_ITEM_CHANGED, "");
        RxBus.get().post(FBEventAction.ACTION_REMOVE_STICKER_RECT,"");

        if (watermarkList != null && watermarkList.getWatermarks() != null && watermarkList.getWatermarks().size() != 0) {
            items.addAll(watermarkList.getWatermarks());
            initRecyclerView();
        } else {
            FBConfigTools.getInstance().getWatermarksConfig(new FBConfigCallBack<List<FBWatermarkConfig.FBWatermark>>() {
                @Override public void success(List<FBWatermarkConfig.FBWatermark> list) {
                    items.addAll(list);
                    initRecyclerView();
                }

                @Override public void fail(Exception error) {
                    Looper.prepare();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            });
        }

    }
    @Subscribe(thread = EventThread.MAIN_THREAD,
               tags = { @Tag(FBEventAction.ACTION_SYNC_WATERMARK_ITEM_CHANGED) })
    public void changedPoint(Object o) {
        int lastposition = FBSelectedPosition.POSITION_WATERMARK;
        FBSelectedPosition.POSITION_WATERMARK = -1;
        watermarkAdapter.notifyItemChanged(lastposition);

    }

    private void initRecyclerView() {
        RecyclerView fbWatermarkRV = (RecyclerView) findViewById(R.id.fbRecyclerView);
        watermarkAdapter = new FBWatermarkAdapter(items, new waterMarkClick() {
            @Override public void clickWaterMarkFromDisk() {
                //调起相册
                openAlbum();
            }

            @Override public void deleteWaterMarkFromDisk(int position) {
                deleteWatermark(position);
            }
        });
        fbWatermarkRV.setLayoutManager(new GridLayoutManager(getContext(), 5));
        fbWatermarkRV.setAdapter(watermarkAdapter);
        fbWatermarkRV.setBackground(ContextCompat.getDrawable(getContext(),
                R.color.dark_background));
    }

    /*
    删除水印
     */
    private void deleteWatermark(int position) {
        items.remove(items.get(position));
        // FBConfigTools.getInstance().watermarkDownload(new Gson().toJson(items));
        // watermarkAdapter.selectItem(0);
        watermarkAdapter.notifyDataSetChanged();
    }

    public interface waterMarkClick {
        //点击从相册中选择背景
        void clickWaterMarkFromDisk();
        //删除从相册选择的背景
        void deleteWaterMarkFromDisk(int position);

    }


    /**
     * 打开相册
     */
    private void openAlbum() {
        //检测权限
        if (ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
        //调取系统相册
        Intent intent = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getData() == null) return;
        if (requestCode == IMAGE_REQUEST_CODE) {
            String imagePath = FBUploadBitmapUtils.handleImageBeforeKitKat(data, getActivity());

            if(imagePath != null){
                FBWatermarkConfig.FBWatermark fbWatermark = new FBWatermarkConfig.FBWatermark("", "", FBDownloadState.COMPLETE_DOWNLOAD,"");
                fbWatermark.setFromDisk(true, requireContext(), imagePath);
                items.add(fbWatermark);
                watermarkAdapter.selectItem(items.size() - 1);
                Bitmap bitmap = BitmapFactory.decodeFile(fbWatermark.getIcon());
                RxBus.get().post(FBEventAction.ACTION_REMOVE_STICKER_RECT,"");
                RxBus.get().post(FBEventAction.ACTION_ADD_STICKER_RECT,bitmap);
                watermarkAdapter.notifyDataSetChanged();
            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FBSelectedPosition.POSITION_WATERMARK=-1;
        FBEffect.shareInstance().setARItem(FBItemEnum.FBItemWatermark.getValue(),"");
    }
}
