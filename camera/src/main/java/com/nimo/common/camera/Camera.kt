package com.nimo.common.camera

import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.util.Log
import com.nimo.common.entity.CameraConfig
import com.nimo.common.enums.CameraTypeEnum
import com.nimo.common.interfaces.ICamera
import com.nimo.common.listener.OnCameraListener


/**
 *相机封装类
 */
class Camera private constructor() : ICamera {
    companion object {
        const val TAG = "FBCamera"

        @Volatile
        private var INSTANCE: Camera? = null

        @JvmStatic
        fun getInstance(): Camera {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = Camera()
                    }
                }
            }
            return INSTANCE!!
        }

    }

    /*相机配置*/
    private var mCameraConfig: CameraConfig? = null


    private var mOnCameraListener: OnCameraListener? = null

    private var mCamera: BaseCamera? = null

    private var currentPreviewData: CameraPreviewData? = null

    @Volatile
    private var isCameraOpen = false

    @Volatile
    private var isSwitchCamera = false

    private var mFPSNumber = 0


    /**
     * 开启相机
     * @param config CameraConfig
     * @param onCameraListener OnCameraListener?
     */
    override fun openCamera(config: CameraConfig, texId: Int, onCameraListener: OnCameraListener?) {
        startBackgroundThread()
        mBackgroundHandler?.post {
            try {
                Log.i(TAG, "openCamera")
                isNeedFPSLoop = true
                mCameraConfig = config
                mOnCameraListener = onCameraListener
                if (isCameraOpen) {
                    mCamera?.closeCamera()
                }
                mCamera = initCamera(config, texId)
                mCamera?.openCamera()
                isCameraOpen = true
            } catch (e:Exception){
                Log.e(TAG,"camera open error" ,e)
                e.printStackTrace()
            }
        }
    }

    /**
     * 关闭相机
     */
    override fun closeCamera() {
        mBackgroundHandler?.post {
            try {
                Log.i(TAG, "closeCamera")
                stopFPSLooper()
                mCameraConfig = null
                mOnCameraListener = null
                currentPreviewData = null
                if (isCameraOpen) {
                    mCamera?.closeCamera()
                    mCamera = null
                    isCameraOpen = false

                }
            } catch (e:Exception){
                Log.e(TAG,"camera close error" ,e)
                e.printStackTrace()
            }
        }
    }


    /**
     * 前后摄像头切换
     */
    override fun switchCamera() {
        if (isSwitchCamera) {
            Log.e(TAG, "switchCamera so frequently")
            return
        }
        isSwitchCamera = true
        mBackgroundHandler?.post {
            Log.i(TAG, "switchCamera")
            mCamera?.switchCamera()
            isCameraOpen = true
            isSwitchCamera = false
        }

    }

    // 释放相机资源
    override fun releaseCamera() {
        Log.i(TAG, "releaseCamera")
        stopBackgroundThread()
    }

    // 初始化相机参数
    private fun initCamera(config: CameraConfig, texId: Int): BaseCamera {
        val camera = if (config.cameraType == CameraTypeEnum.CAMERA1) {
            Camera1(mCameraListener)
        } else {
            Camera2(mCameraListener)
        }
        Log.d(TAG, "initCamera: "+config.cameraType)
        mFPSNumber = config.cameraFPS
        camera.mCameraTexId = texId
        camera.mCameraFacing = config.cameraFacing
        camera.mCameraHeight = config.cameraHeight
        camera.mCameraWidth = config.cameraWidth
        camera.mIsHighestRate = config.isHighestRate
        camera.initCameraInfo()
        return camera
    }


    /*后台线程*/
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundHandlerThread: HandlerThread? = null

    /**
     * 开启后台任务线程
     */
    private fun startBackgroundThread() {
        if (mBackgroundHandler == null) {
            mBackgroundHandlerThread = HandlerThread("$TAG-CAMERA", Process.THREAD_PRIORITY_BACKGROUND)
            mBackgroundHandlerThread!!.start()
            mBackgroundHandler = Handler(mBackgroundHandlerThread!!.looper)
        }
    }

    /**
     * 关闭后台任务线程
     */
    private fun stopBackgroundThread() {
        mBackgroundHandlerThread?.quitSafely()
        mBackgroundHandlerThread = null
        mBackgroundHandler = null
    }

    /**
     *  获取相机朝向
     * @return Int
     */
    override fun getCameraFacing() = currentPreviewData?.cameraFacing

    /**
     *  获取相机宽度
     * @return Int
     */
    override fun getCameraWidth() = currentPreviewData?.width ?: 0

    /**
     * 获取相机高度
     * @return Int
     */
    override fun getCameraHeight() = currentPreviewData?.height ?: 0


    /**
     * 获取相机最新帧数据
     * @return ByteArray?
     */
    override fun getCameraByte() = currentPreviewData

    /**
     * 获取相机绑定的SurfaceTexture
     * @return SurfaceTexture?
     */
    override fun getSurfaceTexture(): SurfaceTexture? {
        return mCamera?.mSurfaceTexture
    }


    /**
     * 设置分辨率
     * @param width Int
     * @param height Int
     */
    override fun changeResolution(width: Int, height: Int) {
        Log.i(TAG, "changeResolution  width:$width   height:$height")
        mBackgroundHandler?.post {
            mCamera?.mCameraWidth = width
            mCamera?.mCameraHeight = height
            mCamera?.changeResolution(width, height)
        }
    }


    /**
     * 对焦
     * @param rawX Float
     * @param rawY Float
     * @param areaSize Int
     */
    override fun handleFocus(viewWidth: Int, viewHeight: Int, rawX: Float, rawY: Float, areaSize: Int) {
        Log.i(TAG, "handleFocus   viewWidth:$viewWidth   viewHeight:$viewHeight   rawX:$rawX  rawY:$rawY  areaSize:$areaSize")
        mBackgroundHandler?.post {
            mCamera?.handleFocus(viewWidth, viewHeight, rawX, rawY, areaSize)
        }
    }

    /**
     * 获取当前光照补偿值
     */
    override fun getExposureCompensation(): Float {
        Log.i(TAG, "getExposureCompensation")
        return mCamera?.getExposureCompensation() ?: 0f
    }

    /**
     * 设置光照补偿
     * @param value Float
     */
    override fun setExposureCompensation(value: Float) {
        Log.i(TAG, "setExposureCompensation  value:$value")
        mBackgroundHandler?.post {
            mCamera?.setExposureCompensation(value)
        }
    }

    /**
     * 设置焦距
     */
    override fun setZoomValue(value: Float) {
        mBackgroundHandler?.post {
            mCamera?.setZoom(value)
        }
    }

    override fun getCamera(): BaseCamera? = mCamera


    private val mFPSThreadLock = Any()

    private var mFPSThread: Thread? = null
    private var isFPSLoop = false //循环标识
    private var isNeedFPSLoop = false

    /**
     * 开启循环
     */
    private fun startFPSLooper() {
        Log.i(TAG, "startFPSLooper")
        synchronized(mFPSThreadLock) {
            isFPSLoop = true
            if (mFPSThread == null) {
                mFPSThread = Thread { doSendPreviewFrame(mFPSNumber) }
                mFPSThread!!.start()
            }
        }
    }

    /**
     * 关闭循环
     */
    private fun stopFPSLooper() {
        Log.i(TAG, "stopFPSLooper")
        synchronized(mFPSThreadLock) {
            isFPSLoop = false
            mFPSThread?.interrupt()
            mFPSThread = null
        }

    }

    private fun doSendPreviewFrame(fps: Int) {
        var first = true
        var startWhen: Long = 0
        val timeStamp = 1000 / 10.coerceAtLeast(100.coerceAtMost(fps)).toLong()
        while (true) {
            if (!isFPSLoop) {
                break
            }
            if (first) {
                first = false
            } else {
                try {
                    val sleepTime: Long = timeStamp - (System.currentTimeMillis() - startWhen)
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime)
                    }
                } catch (e: InterruptedException) {
                    // ignored
                }
            }
            startWhen = System.currentTimeMillis()
            if (currentPreviewData != null && isFPSLoop) {
                Log.i(TAG, "onPreviewFrame")
                mOnCameraListener?.onPreviewFrame(currentPreviewData!!)
            }
        }
    }


    /**
     * Camera 数据回传处理
     */
    private val mCameraListener = object : OnCameraListener {
        override fun onPreviewFrame(previewData: CameraPreviewData) {

            if (!isCameraOpen) {
                isCameraOpen = true
            }
            currentPreviewData = previewData
            if (mFPSNumber <= 0) {
                Log.i(TAG, "onPreviewFrame")
                mOnCameraListener?.onPreviewFrame(previewData)
            } else if (!isFPSLoop && isNeedFPSLoop) {
                startFPSLooper()
            }
        }
    }

}