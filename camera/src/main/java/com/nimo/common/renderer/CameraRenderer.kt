package com.nimo.common.renderer

import android.content.Context
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.MotionEvent
import com.nimo.common.FBApplication
import com.nimo.common.camera.Camera
import com.nimo.common.camera.CameraPreviewData
import com.nimo.common.entity.CameraConfig
import com.nimo.common.entity.RenderFrameData
import com.nimo.common.entity.RenderInputData
import com.nimo.common.enums.CameraFacingEnum
import com.nimo.common.enums.ExternalInputEnum
import com.nimo.common.enums.InputBufferEnum
import com.nimo.common.enums.InputTextureEnum
import com.nimo.common.enums.TransformMatrixEnum
import com.nimo.common.interfaces.ICameraRenderer
import com.nimo.common.listener.OnCameraListener
import com.nimo.common.listener.OnGlRendererListener
import com.nimo.common.media.photo.OnPhotoRecordingListener
import com.nimo.common.media.photo.PhotoRecordHelper
import com.nimo.common.program.ProgramTextureOES
import com.nimo.common.utils.DecimalUtils
import com.nimo.common.utils.GlUtil
import com.nimo.facebeauty.FBEffect
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs


/**
 *
 * DESC：默认配置GlSurfaceRenderer 实现相机跟特效功能
 *
 */

class CameraRenderer(gLSurfaceView: GLSurfaceView?, private val cameraConfig: CameraConfig, glRendererListener: OnGlRendererListener?) :
    BaseRenderer(gLSurfaceView, glRendererListener), ICameraRenderer {

    /**相机**/
    var fBCamera: Camera = Camera.getInstance()
    @Volatile
    var isCameraPreviewFrame = false

    /**传感器**/
    private val mSensorManager by lazy { FBApplication.getApplication().getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val mSensor by lazy { mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    /**渲染配置**/
    private val mRenderInputDataLock = Any()
    private var mProgramTextureOES: ProgramTextureOES? = null
//    private var isFrontCamera = true;

    //特殊设备设置特殊前后置纹理矩阵
    var speOriginFoundTexMatrix:FloatArray? = null //原始图形纹理矩阵
    var speOriginBackTexMatrix:FloatArray? = null //原始图形纹理矩阵

    // 初始化

    init {
        externalInputType = ExternalInputEnum.EXTERNAL_INPUT_TYPE_CAMERA
        inputTextureType = InputTextureEnum.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE
        inputBufferType = InputBufferEnum.FU_FORMAT_NV21_BUFFER
        gLSurfaceView?.setEGLContextClientVersion(GlUtil.getSupportGlVersion(FBApplication.getApplication()))
        gLSurfaceView?.setRenderer(this)
        gLSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    /**
     * 绑定相机回调
     * 更新矩阵
     * 更新相机配置
     * 更新渲染参数
     * @return <no name provided>
     */
    private fun getCameraListener() = object : OnCameraListener {
        override fun onPreviewFrame(previewData: CameraPreviewData) {
            synchronized(mRenderInputDataLock) {
                if (originalWidth != previewData.width || originalHeight != previewData.height) {
                    originalWidth = previewData.width
                    originalHeight = previewData.height
                    defaultMvpMatrix =
                        GlUtil.changeMvpMatrixCrop(surfaceViewWidth.toFloat(), surfaceViewHeight.toFloat(), originalHeight.toFloat(), originalWidth.toFloat())
                    smallViewMatrix = GlUtil.changeMvpMatrixCrop(90f, 160f, originalHeight.toFloat(), originalWidth.toFloat())
                }
                cameraConfig.cameraFacing = previewData.cameraFacing
//                cameraConfig.cameraHeight = previewData.height
//                cameraConfig.cameraWidth = previewData.width
                currentRenderInputData = RenderInputData(originalWidth, originalHeight)
                    .apply {
                        imageBuffer = RenderInputData.FBImageBuffer(inputBufferType, previewData.buffer)
                        texture = RenderInputData.FBTexture(inputTextureType, originalTextId)
//                        Log.d("原始纹理ID", "onPreviewFrame: "+originalTextId)
                        renderConfig.apply {
                            externalInputType = this@CameraRenderer.externalInputType
                            inputOrientation = previewData.cameraOrientation
                            deviceOrientation = this@CameraRenderer.deviceOrientation
                            cameraFacing = previewData.cameraFacing
                            if (cameraFacing == CameraFacingEnum.CAMERA_FRONT) {
                                isFrontCamera = true
                                originTexMatrix = if (speOriginFoundTexMatrix != null)
                                    speOriginFoundTexMatrix!!
                                else
                                    DecimalUtils.copyArray(CAMERA_TEXTURE_MATRIX)
                                inputTextureMatrix = TransformMatrixEnum.CCROT90_FLIPHORIZONTAL
                                inputBufferMatrix = TransformMatrixEnum.CCROT90_FLIPHORIZONTAL
                            } else {
                                isFrontCamera = false
                                originTexMatrix = if (speOriginBackTexMatrix != null)
                                    speOriginBackTexMatrix!!
                                else
                                    DecimalUtils.copyArray(CAMERA_TEXTURE_MATRIX_BACK)
                                inputTextureMatrix = TransformMatrixEnum.CCROT270
                                inputBufferMatrix = TransformMatrixEnum.CCROT270
                            }
                        }
                    }
                isCameraPreviewFrame = true
            }
            gLSurfaceView?.requestRender()
        }
    }

    /**Activity onResume**/
    override fun onResume() {
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
        if (isActivityPause) {
            gLSurfaceView?.onResume()
        }
        isActivityPause = false
    }


    /**Activity onPause**/
    override fun onPause() {
        isActivityPause = true
        mSensorManager.unregisterListener(mSensorEventListener)
        fBCamera.closeCamera()
//        releaseTextureOESRenderer()
        init = false;
        val countDownLatch = CountDownLatch(1)
        gLSurfaceView?.queueEvent {
            cacheLastBitmap()
            destroyGlSurface()
            countDownLatch.countDown()
        }
        try {
            countDownLatch.await(500, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            // ignored
        }
        gLSurfaceView?.onPause()
    }

    /**Activity onDestroy**/
    override fun onDestroy() {
        mCacheBitmap = null
        glRendererListener = null
        gLSurfaceView = null
        releaseTextureOESRenderer()
    }

    //  GLSurfaceView.Renderer相关

    override fun surfaceCreated(gl: GL10?, config: EGLConfig?) {
        originalTextId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES)
        mProgramTextureOES = ProgramTextureOES()
        isCameraPreviewFrame = false
        fBCamera.openCamera(cameraConfig, originalTextId, getCameraListener())
    }

    override fun surfaceChanged(gl: GL10?, width: Int, height: Int) {
        defaultMvpMatrix = GlUtil.changeMvpMatrixCrop(width.toFloat(), height.toFloat(), originalHeight.toFloat(), originalWidth.toFloat())
    }

    override fun updateTexImage() {
        val surfaceTexture = fBCamera.getSurfaceTexture()
        try {
            surfaceTexture?.updateTexImage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun prepareRender(gl: GL10?): Boolean {
        //再检查其他
        if (!isCameraPreviewFrame) {
            drawCacheBitmap()
            return false
        }
        if (mProgramTextureOES == null || programTexture2d == null) {
            return false
        }
        return true
    }

    override fun buildRenderInputData(): RenderInputData {
        synchronized(mRenderInputDataLock) {
            currentRenderInputData.clone()
            //切换相机的时候忽略几帧不处理，
            if (openCameraIgnoreFrame > 0) {
                openCameraIgnoreFrame--
                currentRenderInputData.imageBuffer = null
                currentRenderInputData.texture = null
            }
            return currentRenderInputData
        }
    }

    override fun onRenderBefore(input: RenderInputData, fbRenderFrameData: RenderFrameData) {
        if (input.imageBuffer?.inputBufferType == InputBufferEnum.FU_FORMAT_YUV_BUFFER && input.renderConfig.isNeedBufferReturn) {
            fbRenderFrameData.texMatrix = TEXTURE_MATRIX_CCRO_FLIPV_0_CENTER.copyOf()
            input.renderConfig.outputMatrix = TransformMatrixEnum.CCROT0_FLIPVERTICAL
            input.renderConfig.outputMatrixEnable = true
        }
    }

    override fun drawRenderFrame(gl: GL10?) {
         // 从单位矩阵复制一份
        val texMatrix = getCameraTexMatrix()
        if (fb2DTexId > 0 && renderSwitch) {
            programTexture2d!!.drawFrame(fb2DTexId, texMatrix, defaultMvpMatrix)
        } else if (originalTextId > 0) {
            mProgramTextureOES!!.drawFrame(originalTextId, originTexMatrix, defaultMvpMatrix)
        }
        if (drawSmallViewport) {
            GLES20.glViewport(smallViewportX, smallViewportY, smallViewportWidth, smallViewportHeight)
            mProgramTextureOES!!.drawFrame(originalTextId, originTexMatrix, smallViewMatrix)
            GLES20.glViewport(0, 0, surfaceViewWidth, surfaceViewHeight)
        }
    }


    override fun destroyGlSurface() {
        mProgramTextureOES?.let {
            it.release()
            mProgramTextureOES = null
        }
        deleteCacheBitmapTexId()
        super.destroyGlSurface()
    }

    /**
     * 窗口渲染固定图片
     * @param bitmap Bitmap
     */
    override fun showImageTexture(bitmap: Bitmap) {
        drawImageTexture(bitmap)
    }

    /**
     * 移除图片渲染
     */
    override fun hideImageTexture() {
        dismissImageTexture()
    }

    /**
     * 全身Avatar小窗口显示
     * @param isShow Boolean
     */
    override fun drawSmallViewport(isShow: Boolean) {
        drawSmallViewport = isShow
    }

    override fun setFURenderSwitch(isOpen: Boolean) {
        TODO("Not yet implemented")
        renderSwitch = false
    }


    /**
     * avatar 小窗拖拽
     * @param x Int
     * @param y Int
     * @param action Int
     */
    override fun onTouchEvent(x: Int, y: Int, action: Int) {
        if (!drawSmallViewport) {
            return
        }
        if (action == MotionEvent.ACTION_MOVE) {
            if (x < smallViewportHorizontalPadding || x > surfaceViewWidth - smallViewportHorizontalPadding || y < smallViewportTopPadding || y > surfaceViewHeight - smallViewportBottomPadding
            ) {
                return
            }
            val touchX = touchX
            val touchY = touchY
            this.touchX = x
            this.touchY = y
            val distanceX = x - touchX
            val distanceY = y - touchY
            var viewportX = smallViewportX
            var viewportY = smallViewportY
            viewportX += distanceX
            viewportY -= distanceY
            if (viewportX < smallViewportHorizontalPadding || viewportX + smallViewportWidth > surfaceViewWidth - smallViewportHorizontalPadding || surfaceViewHeight - viewportY - smallViewportHeight < smallViewportTopPadding || viewportY < smallViewportBottomPadding
            ) {
                return
            }
            smallViewportX = viewportX
            smallViewportY = viewportY
        } else if (action == MotionEvent.ACTION_DOWN) {
            touchX = x
            touchY = y
        } else if (action == MotionEvent.ACTION_UP) {
            val alignLeft = smallViewportX < surfaceViewWidth / 2
            smallViewportX = if (alignLeft) smallViewportHorizontalPadding else surfaceViewWidth - smallViewportHorizontalPadding - smallViewportWidth
            touchX = 0
            touchY = 0
        }
    }

    /**
     * 重新开启相机
     */
    override fun reopenCamera() {
        fBCamera.openCamera(cameraConfig, originalTextId, getCameraListener())
    }

    /**
     * 关闭相机
     */
    override fun closeCamera() {
        fBCamera.closeCamera()
    }

    /**
     * 开启camera时过滤的帧数
     */
    var openCameraIgnoreFrame:Int = 0

    /**
     * 切换相机
     */
    override fun switchCamera() {
        openCameraIgnoreFrame = 2
        fBCamera.switchCamera()
    }

    /**
     * 内置陀螺仪
     */
    private val mSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                if (abs(x) > 3 || abs(y) > 3) {
                    deviceOrientation = if (abs(x) > abs(y)) {
                        if (x > 0) 0 else 180
                    } else {
                        if (y > 0) 90 else 270
                    }

                }
            }
        }
    }


// 图像缓存

    private var mCacheBitmap: Bitmap? = null //图片资源
    private var mCacheBitmapTexId = 0
    private var mCacheBitmapMvpMatrix = TEXTURE_MATRIX.copyOf()

    private val mOnPhotoRecordingListener by lazy {
        OnPhotoRecordingListener {
            mCacheBitmap = it
        }
    }

    private val mPhotoRecordHelper = PhotoRecordHelper(mOnPhotoRecordingListener)


    private fun cacheLastBitmap() {
        val texMatrix = getCameraTexMatrix()
        if (currentRenderOutputData != null && currentRenderOutputData!!.texture != null) {
//            mPhotoRecordHelper.sendRecordingData(fb2DTexId, currentTexMatrix, TEXTURE_MATRIX, currentRenderOutputData!!.texture!!.width, currentRenderOutputData!!.texture!!.height)
            mPhotoRecordHelper.sendRecordingData(fb2DTexId, texMatrix, TEXTURE_MATRIX, currentRenderOutputData!!.texture!!.height, currentRenderOutputData!!.texture!!.width)
        }
    }

    //todo --faceBeauty--start
    /**
     * 获取摄像头纹理矩阵（前置：270° + 镜像；后置：90° + 修正）
     */
    private fun getCameraTexMatrix(): FloatArray {
        val texMatrix = TEXTURE_MATRIX.copyOf()
        if (isFrontCamera) {
            // 前置摄像头：270° + 水平镜像
            Matrix.rotateM(texMatrix, 0, 270f, 0f, 0f, 1f)
        } else {
            // 后置摄像头：90°
            Matrix.rotateM(texMatrix, 0, 90f, 0f, 0f, 1f)
            Matrix.scaleM(texMatrix, 0, -1f, 1f, 1f) // 水平镜像修正
        }
        return texMatrix
    }
    //todo --faceBeauty--end

    private fun drawCacheBitmap() {
        mCacheBitmap?.let {
            deleteCacheBitmapTexId()
            mCacheBitmapTexId = GlUtil.createImageTexture(it)
            mCacheBitmapMvpMatrix =
                GlUtil.changeMvpMatrixCrop(surfaceViewWidth.toFloat(), surfaceViewHeight.toFloat(), it.width.toFloat(), it.height.toFloat())
            Matrix.scaleM(mCacheBitmapMvpMatrix, 0, 1f, -1f, 1f)
            if (mCacheBitmapTexId > 0) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
                programTexture2d?.drawFrame(mCacheBitmapTexId, TEXTURE_MATRIX, mCacheBitmapMvpMatrix)
            }
        }
    }

    /**
     * 移除图片纹理
     */
    private fun deleteCacheBitmapTexId() {
        if (mCacheBitmapTexId > 0) {
            GlUtil.deleteTextures(intArrayOf(mCacheBitmapTexId))
            mCacheBitmapTexId = 0
        }
    }

    private fun releaseTextureOESRenderer(){
        FBEffect.shareInstance().releaseTextureOESRenderer()
        FBEffect.shareInstance().removeAIProcessor(0)
        FBEffect.shareInstance().removeAIProcessor(1)
        init = false

    }

}