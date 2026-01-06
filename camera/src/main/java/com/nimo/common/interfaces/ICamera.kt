package com.nimo.common.interfaces

import android.graphics.SurfaceTexture
import com.nimo.common.camera.BaseCamera
import com.nimo.common.camera.CameraPreviewData
import com.nimo.common.entity.CameraConfig
import com.nimo.common.enums.CameraFacingEnum
import com.nimo.common.listener.OnCameraListener


/**
 *
 * Camera接口定义
 *
 */
interface ICamera {

    /**
     * 获取相机朝向
     * @return CameraFacing
     */
    fun getCameraFacing(): CameraFacingEnum?

    /**
     *  获取相机宽度
     * @return Int
     */
    fun getCameraWidth(): Int

    /**
     * 获取相机高度
     * @return Int
     */
    fun getCameraHeight(): Int

    /**
     * 获取相机绑定的SurfaceTexture
     * @return SurfaceTexture?
     */
    fun getSurfaceTexture(): SurfaceTexture?

    /**
     * 获取相机最新帧数据
     * @return ByteArray?
     */
    fun getCameraByte(): CameraPreviewData?


    /**
     * 打开相机后直接开启预览模式
     */
    fun openCamera(config: CameraConfig, texId: Int, onCameraListener: OnCameraListener?)

    /**
     * 关闭相机
     */
    fun closeCamera()

    /**
     * 释放相机资源
     */
    fun releaseCamera()


    /**
     * 前后摄像头切换
     */
    fun switchCamera()

    /**
     * 设置分辨率
     * @param width Int
     * @param height Int
     */
    fun changeResolution(width: Int, height: Int)


    /**
     * 对焦
     * @param rawX Float
     * @param rawY Float
     * @param areaSize Int
     */
    fun handleFocus(viewWidth: Int, viewHeight: Int, rawX: Float, rawY: Float, areaSize: Int)

    /**
     * 获取当前光照补偿值
     */
    fun getExposureCompensation(): Float

    /**
     * 设置光照补偿
     * @param value Float
     */
    fun setExposureCompensation(value: Float)

    /**
     * 设置焦距
     */
    fun setZoomValue(value:Float)

    /**
     * 获取真实的Camera
     */
    fun getCamera(): BaseCamera?
}