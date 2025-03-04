package com.nimo.facebeauty.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nimo.fb_effect.model.FBViewState;
import com.nimo.facebeauty.tools.FBCamera;
import com.nimo.facebeauty.view.FBGlSurfaceView;
import com.nimo.fb_effect.FBPanelLayout;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.FBTextureToByteBuffer;
import com.nimo.facebeauty.FBPreviewRenderer;
import com.nimo.facebeauty.egl.FBGLUtils;
import com.nimo.facebeauty.model.FBRotationEnum;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SampleCameraActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

  private String TAG = "GLSurfaceViewCameraActivity";

  private FBGlSurfaceView glSurfaceView;
  private FBCamera camera;

  private FBPreviewRenderer showRenderer;
  private FBTextureToByteBuffer textureToByteBuffer;

  private SurfaceTexture surfaceTexture;
  private int oesTextureId;

  /**
   * 相机采集的宽高
   */
  private final int imageWidth = 1280;
  private final int imageHeight = 720;

  private final boolean isFrontCamera = true;
  private FBRotationEnum tiRotation;
  private int cameraId;

  private boolean isTakePicture = false;
  private int pictureWidth = 720, pictureHeight = 1280;
  private String picturePath;
  private HandlerThread pictureHandlerThread;
  private Handler pictureHandler;

  protected boolean init = false;

  /**
   * 页面显示的宽高
   */
  private int surfaceWidth, surfaceHeight;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    glSurfaceView = new FBGlSurfaceView(this);
    glSurfaceView.setEGLContextClientVersion(2);
    glSurfaceView.setRenderer(this);
    glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    setContentView(glSurfaceView);
    //todo ---Face Beauty start----
    FBEffect.shareInstance().releaseTextureOESRenderer();
    FBPanelLayout FBPanelLayout = new FBPanelLayout(this).init(getSupportFragmentManager());
    addContentView(FBPanelLayout,
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    FBPanelLayout.showPanel(FBViewState.BEAUTY);
    //todo ---Face Beauty end----

    Button button = new Button(this);
    button.setText("拍照");
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        takePicture();
      }
    });
    addContentView(button,
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    pictureHandlerThread = new HandlerThread("TakePicture");
    pictureHandlerThread.start();
    pictureHandler = new Handler(pictureHandlerThread.getLooper());
    glSurfaceView.setAspectRatio(pictureWidth, pictureHeight);
    camera = new FBCamera(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    try {
      camera.startPreview();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    try {
      camera.stopPreview();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    init = false;
    //todo ---Face Beauty start----
    FBEffect.shareInstance().releaseTextureOESRenderer();
    //todo ---Face Beauty end----
    pictureHandlerThread.quit();
    camera.releaseCamera();
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    Log.i(TAG, "onSurfaceCreated");
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {

    Log.i(TAG, "onSurfaceChanged width = " + width + ", height = " + height);
    surfaceWidth = width;
    surfaceHeight = height;

    showRenderer = new FBPreviewRenderer(surfaceWidth, surfaceHeight);
    showRenderer.setPreviewRotation(270);
    showRenderer.create(isFrontCamera);

    textureToByteBuffer = new FBTextureToByteBuffer(pictureWidth, pictureHeight, true);
    textureToByteBuffer.create(isFrontCamera);

    oesTextureId = FBGLUtils.getExternalOESTextureID();
    surfaceTexture = new SurfaceTexture(oesTextureId);
    surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
      @Override
      public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        glSurfaceView.requestRender();
      }
    });

    cameraId = isFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
    tiRotation = isFrontCamera ? FBRotationEnum.FBRotationClockwise270 : FBRotationEnum.FBRotationClockwise90;
    camera.openCamera(cameraId, imageWidth, imageHeight);

    camera.setPreviewSurface(surfaceTexture);
    camera.startPreview();
  }

  @Override
  public void onDrawFrame(GL10 gl) {

    //todo ---Face Beauty start----
    if (!init && showRenderer != null) {
      Log.e("初始化:", "------");
      FBEffect.shareInstance().releaseTextureOESRenderer();
      init = FBEffect.shareInstance().initTextureOESRenderer(imageWidth,imageHeight,tiRotation,isFrontCamera,5);
    }

    int textureId = FBEffect.shareInstance().processTextureOES(oesTextureId);


    // Log.e("渲染:", "------------------------");

    if (showRenderer == null) {
      Log.e("ERROR：", "showRender is null");
      return;
    }

    showRenderer.render(textureId);
    //todo ---Face Beauty end----


    if (isTakePicture) {
      ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pictureWidth * pictureHeight * 4);
      byteBuffer.order(ByteOrder.nativeOrder());
      byteBuffer.position(0);
      textureToByteBuffer.takePicture(textureId, byteBuffer);
      saveBitmap(pictureWidth, pictureHeight, byteBuffer);
      isTakePicture = false;
    }
    surfaceTexture.updateTexImage();
  }

  private void takePicture() {
    isTakePicture = true;
  }

  private void saveBitmap(final int width, final int height, final ByteBuffer bf) {

    pictureHandler.post(new Runnable() {
      @Override
      public void run() {

        //根据需要自己调节图片的大小，如果卡顿将质量调低即可
        //                Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(bf);
        picturePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        new File(picturePath).mkdirs();
        picturePath = picturePath + "/" + System.currentTimeMillis() + ".png";

        boolean isSuccess = saveBitmap(result, new File(picturePath));
        Log.e(TAG, "saveBitmap,path:" + picturePath);

      }
    });
  }

  private boolean saveBitmap(Bitmap bitmap, File file) {
    if (bitmap == null) { return false; }
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
      fos.flush();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

}