package com.nimo.facebeauty.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;

import com.nimo.fb_effect.model.FBViewState;
import com.spica.camera.widget.BaseCameraActivity;
import com.nimo.fb_effect.FBPanelLayout;
import java.io.File;

/**
 * 优先Camera2 Api 如果硬件等级低于level3则使用 Camera 1 Api
 */
public class Camera2Activity extends BaseCameraActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //todo ---Face Beauty start----
    FBPanelLayout FBPanelLayout = new FBPanelLayout(this).init(getSupportFragmentManager());
    addContentView(FBPanelLayout,
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
    FBPanelLayout.showPanel(FBViewState.BEAUTY);
    //todo ---Face Beauty end----
  }

  @Override
  public void getOriginalPicture(File file, File thumbFile) {
    //

  }

  @Override
  public void getProcessedPicture(File file) {

  }

  @Override
  public void onVideoRecorded(File file, File thumbFile) {

  }

}