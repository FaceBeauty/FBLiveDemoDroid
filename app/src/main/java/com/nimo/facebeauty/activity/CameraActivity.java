package com.nimo.facebeauty.activity;

import static android.view.View.VISIBLE;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nimo.common.entity.CameraConfig;
import com.nimo.common.entity.RenderFrameData;
import com.nimo.common.entity.RenderInputData;
import com.nimo.common.entity.RenderOutputData;
import com.nimo.common.listener.OnGlRendererListener;
import com.nimo.common.media.photo.OnPhotoRecordingListener;
import com.nimo.common.media.photo.PhotoRecordHelper;
import com.nimo.common.media.video.OnVideoRecordingListener;
import com.nimo.common.media.video.VideoRecordHelper;
import com.nimo.common.renderer.CameraRenderer;

import com.nimo.common.utils.GlUtil;
import com.nimo.facebeauty.FileUtils;
import com.nimo.facebeauty.PermissionsUtils;
import com.nimo.facebeauty.R;
import com.nimo.facebeauty.widget.HtPermissionDialog;

import com.nimo.facebeauty.widget.CaptureButtonView;
import com.nimo.fb_effect.FBPanelLayout;
import com.nimo.fb_effect.fragment.FBBeautyFragment;

import org.jetbrains.annotations.NotNull;

import java.io.File;


public class CameraActivity extends AppCompatActivity{

  private GLSurfaceView glSurfaceView;
  private AppCompatImageView btnSwitch;

  private CaptureButtonView mTakePicView;

  protected CameraRenderer mCameraRenderer;


  private FBPanelLayout FBPanelLayout;
  private Paint KeyPointsPaint = new Paint();


  private final HtPermissionDialog permissionDialog = new HtPermissionDialog();




  @Override
  protected void onCreate(Bundle savedInstanceState) {
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_camera);

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();



    FBPanelLayout = new FBPanelLayout(this).init(getSupportFragmentManager());


    addContentView(FBPanelLayout,
            new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
    initView();
    mVideoRecordHelper = new VideoRecordHelper(this, mOnVideoRecordingListener);
    mPhotoRecordHelper = new PhotoRecordHelper(mOnPhotoRecordingListener);
    //todo --faceBeauty--start
      //添加美颜面板
      FBBeautyFragment beautyFragment = new FBBeautyFragment();
      fragmentTransaction.add(R.id.bottom_container, beautyFragment);
      fragmentTransaction.commit();
      //todo --faceBeauty--end

    //获取屏幕宽高
    DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

    checkPermissions();


    KeyPointsPaint.setColor((Color.WHITE));
    KeyPointsPaint.setStyle(Paint.Style.FILL);
    KeyPointsPaint.setStrokeWidth(2);

  }


  private final Runnable cameraFocusDismiss = () -> {
  };

  private static int dp2px(Context context, float dp) {
    return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
  }

  private DisplayMetrics getScreenInfo() {
    WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    Display defaultDisplay = windowManager.getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    defaultDisplay.getMetrics(displayMetrics);
    return displayMetrics;
  }

  /**
   * 创建监听权限的接口对象
   */
  private PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
    @Override
    public void passPermissons() {
      glSurfaceView.setVisibility(VISIBLE);
      mCameraRenderer = new CameraRenderer(glSurfaceView, getCameraConfig(), mOnGlRendererListener);
    }

    @Override
    public void forbitPermissons() {
      finish();
      //未授权，请手动授权

    }
  };

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //就多一个参数this
    PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
  }
  @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
  private void initView() {

    glSurfaceView = findViewById(R.id.glSurfaceView);

    btnSwitch = findViewById(R.id.btn_switch);

    mTakePicView = findViewById(R.id.iv_shutter);
//todo --faceBeauty--start
    //点击切换前后置
    btnSwitch.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mCameraRenderer.switchCamera();
      }
    });
      //拍照和录制视频
    mTakePicView.setOnRecordListener(mOnRecordListener);
      //todo --faceBeauty--end

  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mCameraRenderer != null){
      mCameraRenderer.onResume();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mCameraRenderer != null){
      mCameraRenderer.onPause();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();

  }



  @Override public void onPointerCaptureChanged(boolean hasCapture) {

  }
  //  @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
  private void checkPermissions() {

    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            permission.READ_PHONE_STATE
    };
    if(PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult)){
      permissionDialog.show(getSupportFragmentManager(),"");
    }

  }
  /* CameraRenderer 回调*/
  private final OnGlRendererListener mOnGlRendererListener = new OnGlRendererListener() {


    private int width;//数据宽
    private int height;//数据高
    private long mFuCallStartTime = 0; //渲染前时间锚点（用于计算渲染市场）


    private int mCurrentFrameCnt = 0;
    private int mMaxFrameCnt = 10;
    private long mLastOneHundredFrameTimeStamp = 0;
    private long mOneHundredFrameFUTime = 0;


    @Override
    public void onSurfaceCreated() {
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
    }

    @Override
    public void onRenderBefore(RenderInputData inputData) {
      mEnableFaceRender = true;
      width = inputData.getWidth();
      height = inputData.getHeight();
      mFuCallStartTime = System.nanoTime();

    }


    @Override
    public void onRenderAfter(@NonNull RenderOutputData outputData, @NotNull RenderFrameData frameData) {
      recordingData(outputData, frameData.getTexMatrix());
    }

    @Override
    public void onDrawFrameAfter() {
    }


    @Override
    public void onSurfaceDestroy() {
    }

    private boolean mEnableFaceRender = false; //是否使用sdk渲染，该变量只在一个线程使用不需要volatile

    /*录制保存*/
    private void recordingData(RenderOutputData outputData, float[] texMatrix) {
      if (outputData == null || outputData.getTexture() == null || outputData.getTexture().getTexId() <= 0) {
        return;
      }
      if (isRecordingPrepared) {
        mVideoRecordHelper.frameAvailableSoon(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX, mCameraRenderer.isFrontCamera());
      }
      if (isTakePhoto) {
        isTakePhoto = false;
        Log.d("摄像机方向", "onClick: "+mCameraRenderer.isFrontCamera());

        mPhotoRecordHelper.sendRecordingData(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX, outputData.getTexture().getWidth(), outputData.getTexture().getHeight(), mCameraRenderer.isFrontCamera());
        Log.d("takePhotoMessage", "recordingData: "+outputData.getTexture().getTexId() + "  "+ texMatrix + "   "+GlUtil.IDENTITY_MATRIX + "  " + outputData.getTexture().getWidth() + "  "+ outputData.getTexture().getHeight());
      }
    }
  };

  //拍照
  private PhotoRecordHelper mPhotoRecordHelper;
  private volatile Boolean isTakePhoto = false;

  /**
   * 获取拍摄的照片
   */
  private final OnPhotoRecordingListener mOnPhotoRecordingListener = this::onReadBitmap;


  protected void onReadBitmap(Bitmap bitmap) {
    new Thread(() -> {
      String path = FileUtils.addBitmapToAlbum(this, bitmap);
      if (path == null) return;
      runOnUiThread(() -> Toast.makeText(CameraActivity.this, "图片保存到相册", Toast.LENGTH_SHORT).show());
    }).start();
  }


  //录制视频
  private VideoRecordHelper mVideoRecordHelper;
  private volatile boolean isRecordingPrepared = false;
  private boolean isRecording = false;
  private volatile long recordTime = 0;


  protected void onStartRecord() {
    mVideoRecordHelper.startRecording(glSurfaceView, mCameraRenderer.getFBCamera().getCameraHeight(), mCameraRenderer.getFBCamera().getCameraWidth());
  }

  protected void onStopRecord() {
    mTakePicView.setSecond(0);
    mVideoRecordHelper.stopRecording();
  }

  private OnVideoRecordingListener mOnVideoRecordingListener = new OnVideoRecordingListener() {

    @Override
    public void onPrepared() {
      isRecordingPrepared = true;
    }

    @Override
    public void onProcess(Long time) {
      recordTime = time;
      runOnUiThread(() -> {
        if (isRecording) {
          mTakePicView.setSecond(time);
        }
      });

    }

    @Override
    public void onFinish(File file) {
      isRecordingPrepared = false;
      if (recordTime < 1100) {
        runOnUiThread(() -> Toast.makeText(CameraActivity.this, "录制视频太短", Toast.LENGTH_SHORT).show());
      } else {
        String filePath = FileUtils.addVideoToAlbum(CameraActivity.this, file);
        if (filePath == null || filePath.trim().length() == 0) {
          runOnUiThread(() -> Toast.makeText(CameraActivity.this, "视频保存失败", Toast.LENGTH_SHORT).show());
        } else {
          runOnUiThread(() -> Toast.makeText(CameraActivity.this, "视频保存成功", Toast.LENGTH_SHORT).show());
        }
      }
      if (file.exists()) {
        file.delete();
      }
    }

  };

  /* 拍照按钮事件回调  */
  private final CaptureButtonView.OnRecordListener mOnRecordListener = new CaptureButtonView.OnRecordListener() {
    @Override
    public void stopRecord() {
      if (isRecording) {
        isRecording = false;
        CameraActivity.this.onStopRecord();
      }
    }

    @Override
    public void startRecord() {
      if (!isRecording) {
        isRecording = true;
        CameraActivity.this.onStartRecord();
      }
    }

    @Override
    public void takePic() {
      isTakePhoto = true;
    }
  };

  protected CameraConfig getCameraConfig() {
    CameraConfig cameraConfig = new CameraConfig();
    return cameraConfig;
  }
}