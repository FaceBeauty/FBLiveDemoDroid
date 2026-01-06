package com.nimo.common.camera

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.nimo.common.listener.OnCameraListener


class CameraDataPool(val listener: OnCameraListener) {

    companion object {
        const val MSG_WHAT = 10
    }

    /**
     * CPU 缓存数据
     */
    @Volatile
    private var mCameraPreviewData: CameraPreviewData? = null

    /**
     * CPU 更新时间戳
     */
    @Volatile
    private var mCameraCPUTime: Long = 0L

    /**
     * CPU 更新时间戳
     */
    @Volatile
    private var mCameraGPUTime: Long = 0L

    /**
     * 数据容错间隔
     */
    private val mDuration = 8000000L//纳秒

    /**
     * 数据容错间隔
     */
    private val mDelayedTime = mDuration / 1000000//纳秒

    /**
     * 更新 CPU 数据
     */
    fun updateCPUData(data: CameraPreviewData) {
        if (mCameraPreviewData == null) {
            mCameraCPUTime = System.nanoTime()
            mCameraPreviewData = data
            mBackgroundHandler?.removeMessages(MSG_WHAT)
            callbackData()
        } else {
            mCameraCPUTime = System.nanoTime()
            mCameraPreviewData = data
            if (mCameraCPUTime - mCameraGPUTime < mDuration) {
                mBackgroundHandler?.removeMessages(MSG_WHAT)
                callbackData()
            }
        }
    }

    /**
     * 更新 GPU 数据
     */
    fun updateGPUData() {
        if (mCameraPreviewData == null) return
        mCameraGPUTime = System.nanoTime()
        if (mCameraGPUTime - mCameraCPUTime < mDuration) {
            mBackgroundHandler?.removeMessages(MSG_WHAT)
            callbackData()
        } else {
            mBackgroundHandler?.removeMessages(MSG_WHAT)
            mBackgroundHandler?.sendEmptyMessageDelayed(MSG_WHAT, mDelayedTime)
        }
    }

    /**
     * 回调数据
     */
    private fun callbackData() {
        mCameraPreviewData?.let {
            listener.onPreviewFrame(it)
        }
    }


    // 线程调度
    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: BackgroundHandler? = null
    private val mBackgroundThreadLock = Any()

    /**
     * 开启后台数据线程
     */
    fun startBackgroundHandle() {
        synchronized(mBackgroundThreadLock) {
            if (mBackgroundThread == null) {
                mBackgroundThread = HandlerThread("Camera1DataPool").apply {
                    start()
                    mBackgroundHandler = BackgroundHandler(this.looper, this@CameraDataPool)
                }
            }
        }
    }

    /**
     * 关闭后台数据线程
     */
    fun stopBackgroundHandle() {
        synchronized(mBackgroundThreadLock) {
            mBackgroundHandler?.removeCallbacksAndMessages(0)
            mBackgroundThread?.quitSafely()
            mBackgroundHandler = null
            mBackgroundThread = null
            mCameraPreviewData = null
        }
    }

    /**
     * 自定义消息 Handler
     */
    private class BackgroundHandler(looper: Looper, val dataLopper: CameraDataPool) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG_WHAT) {
                dataLopper.callbackData()
            }
        }
    }

}