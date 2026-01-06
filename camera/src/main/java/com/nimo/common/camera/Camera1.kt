package com.nimo.common.camera

import com.nimo.common.listener.OnCameraListener
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import com.nimo.common.FBApplication
import com.nimo.common.enums.CameraFacingEnum
import com.nimo.common.utils.CameraUtils

/**
 *
 * Camera1实现类
 *
 */
@Suppress("DEPRECATION")
class Camera1(private val cameraListener: OnCameraListener) : BaseCamera() {

    companion object {
        private const val EXPOSURE_COMPENSATION = 0.5f
    }

    /* 相机对象 */
    private var mCamera: Camera? = null

    /* 曝光补偿，进度 0.5 表示实际值为 0 就是无补偿*/
    private var mExposureCompensation: Float = EXPOSURE_COMPENSATION

    private var mPreviewCallbackBufferArray: Array<ByteArray>? = null

    private val mCameraDataPool by lazy {
        CameraDataPool(object : OnCameraListener {
            override fun onPreviewFrame(previewData: CameraPreviewData) {
                if (!mIsStopPreview) {
                    cameraListener.onPreviewFrame(previewData)
                }
            }
        })
    }

    /**
     * 初始化相机参数
     * mFrontCameraId  mFrontCameraOrientation
     * mBackCameraId mBackCameraOrientation
     * mCameraOrientation
     */
    override fun initCameraInfo() {
        val number = Camera.getNumberOfCameras()
        if (number <= 0) {
            Log.e(TAG, "No camera")
            return
        }

        val cameraInfo = Camera.CameraInfo()
        for (i in 0 until number) {
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mFrontCameraId = i
                mFrontCameraOrientation = cameraInfo.orientation
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mBackCameraId = i
                mBackCameraOrientation = cameraInfo.orientation
            }
        }
        mCameraOrientation = if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) mFrontCameraOrientation else mBackCameraOrientation
    }


    /**
     * 开启相机
     * 初始化 mCameraWidth mCameraHeight
     */
    override fun openCamera() {
        if (mCamera != null) {
            return
        }
        try {
            val isFront = mCameraFacing == CameraFacingEnum.CAMERA_FRONT
            val cameraId = if (isFront) mFrontCameraId else mBackCameraId
            mCamera = Camera.open(cameraId)
            if (mCamera == null) {
                throw java.lang.RuntimeException("No camera")
            }
            mExposureCompensation = EXPOSURE_COMPENSATION
            CameraUtils.setCameraDisplayOrientation(FBApplication.getApplication(), cameraId, mCamera!!)
            val parameters: Camera.Parameters = mCamera!!.parameters
            CameraUtils.setFocusModes(parameters)
            CameraUtils.chooseFrameRate(parameters,mIsHighestRate )
            val size: IntArray = CameraUtils.choosePreviewSize(parameters, mCameraWidth, mCameraHeight)
            mCameraWidth = size[0]
            mCameraHeight = size[1]
            parameters.previewFormat = ImageFormat.NV21
            CameraUtils.setParameters(mCamera, parameters)
            // log camera all parameters
            logCameraParameters()
            mCameraDataPool.startBackgroundHandle()
            startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "openCamera:${e.message}")
        }

    }


    /**
     * 开启预览
     * 初始化 mSurfaceTexture
     */
    override fun startPreview() {
        if (mCameraTexId == 0 || mCamera == null || mIsPreviewing) {
            return
        }
        try {
            mCamera!!.stopPreview()
            if (mPreviewCallbackBufferArray == null) {
                mPreviewCallbackBufferArray = Array(PREVIEW_BUFFER_SIZE) {
                    ByteArray((mCameraWidth * mCameraHeight * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8)
                }
            }
            // must call after stopPreview
            mCamera!!.setPreviewCallbackWithBuffer(mPreviewCallback)
            for (bytes in mPreviewCallbackBufferArray!!) {
                mCamera!!.addCallbackBuffer(bytes)
            }
            mSurfaceTexture = SurfaceTexture(mCameraTexId)
            mSurfaceTexture?.setOnFrameAvailableListener {
                mCameraDataPool.updateGPUData()
            }
            mCamera!!.setPreviewTexture(mSurfaceTexture)

//            mCamera!!.setPreviewTexture(mSurfaceTexture)
            mCamera!!.startPreview()
            mIsPreviewing = true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 关闭相机
     * 释放 mSurfaceTexture
     */
    override fun closeCamera() {
        mIsPreviewing = false
        try {
            if (mCamera != null) {
                mCamera!!.stopPreview()
                mCamera!!.setPreviewTexture(null)
                mCamera!!.setPreviewCallbackWithBuffer(null)
                mCamera!!.release()
                mCamera = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mSurfaceTexture?.release()
        mSurfaceTexture = null
        mCameraDataPool.stopBackgroundHandle()
    }

    /**
     * 相机数据回调
     */
    private val mPreviewCallback = Camera.PreviewCallback { data, _ ->
        mCamera!!.addCallbackBuffer(data)
        if (!mIsStopPreview) {
            data?.let {
                mCameraDataPool.updateCPUData(CameraPreviewData(it, mCameraFacing, mCameraOrientation, mCameraWidth, mCameraHeight))
            }
        }
    }


    /**
     * 对焦
     * @param rawX Float
     * @param rawY Float
     * @param areaSize Int
     */
    override fun handleFocus(viewWidth: Int, viewHeight: Int, rawX: Float, rawY: Float, areaSize: Int) {
        CameraUtils.handleFocusMetering(
            mCamera, rawX, rawY, viewWidth, viewHeight, mCameraWidth, mCameraHeight, areaSize,
            if (mCameraFacing == CameraFacingEnum.CAMERA_FRONT) 1 else 0
        )
    }

    /**
     * 获取当前获取光照补偿
     */
    override fun getExposureCompensation(): Float = mExposureCompensation

    /**
     * 获取光照补偿
     * @param value Float
     */
    override fun setExposureCompensation(value: Float) {
        mExposureCompensation = value
        CameraUtils.setExposureCompensation(mCamera, value)
    }

    /**
     * 设置焦距
     */
    override fun setZoom(value: Float) {
        CameraUtils.setZoom(mCamera, value)
    }

    /**
     * 获取焦距
     */
    override fun getZoom():Float {
        var zoom = 1.0f
        mCamera?.parameters?.zoom?.toFloat()?.let {
            zoom = it
        }
        return zoom
    }

    /**
     * 调整分辨率
     * @param cameraWidth Int
     * @param cameraHeight Int
     */
    override fun changeResolution(cameraWidth: Int, cameraHeight: Int) {
        mIsStopPreview = true
        mPreviewCallbackBufferArray = null
        closeCamera()
        openCamera()
        startPreview()
        mIsStopPreview = false
    }

    /**
     * 打印相机所有参数
     */
    private fun logCameraParameters() {
        if (CameraUtils.DEBUG) {
            val fullCameraParameters: Map<String, String> = CameraUtils.getFullCameraParameters(mCamera!!)
            fullCameraParameters.forEach { (t, u) ->
                Log.d(TAG, "Camera1 parameters   $t:$u")
            }
        }
    }

}