package com.nimo.common.entity

import com.nimo.common.enums.CameraFacingEnum
import com.nimo.common.enums.CameraTypeEnum

/**
 *
 * Camera相关构建参数
 *
 */

/**
 * 相机配置类
 *
 * 用于描述相机初始化和打开时的各项参数，包括：
 * - 相机类型（Camera1 / Camera2）
 * - 前后摄像头选择
 * - 输出分辨率
 * - 帧率控制策略
 */
class CameraConfig {

    /**
     * 相机类型
     *
     * 默认为 Camera1
     */
    @JvmField
    var cameraType: CameraTypeEnum = CameraTypeEnum.CAMERA1

    /**
     * 摄像头朝向
     *
     * CAMERA_FRONT：前置摄像头
     * CAMERA_BACK ：后置摄像头
     */
    @JvmField
    var cameraFacing: CameraFacingEnum = CameraFacingEnum.CAMERA_FRONT

    /**
     * 相机输出帧率
     *
     * 单位：fps
     * -1 表示不限制帧率，由系统或相机驱动自行决定
     */
    @JvmField
    var cameraFPS: Int = -1

    /**
     * 相机输出分辨率宽度
     *
     * 单位：像素（px）
     */
    @JvmField
    var cameraWidth: Int = 1280

    /**
     * 相机输出分辨率高度
     *
     * 单位：像素（px）
     */
    @JvmField
    var cameraHeight: Int = 720

    /**
     * 是否开启相机支持的最高帧率
     *
     * true  ：尝试使用设备支持的最高帧率
     * false ：使用 cameraFPS 指定的帧率或系统默认策略
     */
    @JvmField
    var isHighestRate: Boolean = false

    /**
     * 设置相机类型
     *
     * @param cameraType 相机类型（Camera1 / Camera2）
     * @return 当前 CameraConfig 实例，支持链式调用
     */
    fun setCameraType(cameraType: CameraTypeEnum): CameraConfig {
        this.cameraType = cameraType
        return this
    }

    /**
     * 设置摄像头朝向
     *
     * @param cameraFacing 前置或后置摄像头
     * @return 当前 CameraConfig 实例，支持链式调用
     */
    fun setCameraFacing(cameraFacing: CameraFacingEnum): CameraConfig {
        this.cameraFacing = cameraFacing
        return this
    }

    /**
     * 设置相机输出帧率
     *
     * @param cameraFPS 帧率值（fps），-1 表示不限制
     * @return 当前 CameraConfig 实例，支持链式调用
     */
    fun setCameraFPS(cameraFPS: Int): CameraConfig {
        this.cameraFPS = cameraFPS
        return this
    }

    /**
     * 设置相机输出分辨率高度
     *
     * @param cameraHeight 分辨率高度（px）
     * @return 当前 CameraConfig 实例，支持链式调用
     */
    fun setCameraHeight(cameraHeight: Int): CameraConfig {
        this.cameraHeight = cameraHeight
        return this
    }

    /**
     * 设置相机输出分辨率宽度
     *
     * @param cameraWidth 分辨率宽度（px）
     * @return 当前 CameraConfig 实例，支持链式调用
     */
    fun setCameraWidth(cameraWidth: Int): CameraConfig {
        this.cameraWidth = cameraWidth
        return this
    }

    /**
     * 设置是否启用相机最高帧率模式
     *
     * @param isHighestRate true 使用最高帧率，false 使用普通模式
     * @return 当前 CameraConfig 实例，支持链式调用
     */
    fun setHighestRate(isHighestRate: Boolean): CameraConfig {
        this.isHighestRate = isHighestRate
        return this
    }
}