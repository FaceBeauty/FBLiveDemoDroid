package com.nimo.common.entity

import com.nimo.common.enums.CameraFacingEnum
import com.nimo.common.enums.ExternalInputEnum
import com.nimo.common.enums.InputBufferEnum
import com.nimo.common.enums.InputTextureEnum
import com.nimo.common.enums.TransformMatrixEnum


data class RenderInputData(
    var width: Int,//宽
    var height: Int//高
) {
    var texture: FBTexture? = null//数据参数
    var imageBuffer: FBImageBuffer? = null//数据参数
    var renderConfig = FBRenderConfig()//渲染配置

    data class FBTexture(
        var inputTextureType: InputTextureEnum,//纹理类型
        var texId: Int//纹理id
    )

    data class FBImageBuffer @JvmOverloads constructor(
        var inputBufferType: InputBufferEnum,//数据Buffer类型
        var buffer: ByteArray? = null,//数据Buffer
        var buffer1: ByteArray? = null,//YUV_BUFFER 对应U-Buffer
        var buffer2: ByteArray? = null//YUV_BUFFER 对应V-Buffer
    )

    class FBRenderConfig @JvmOverloads constructor(
        var externalInputType: ExternalInputEnum = ExternalInputEnum.EXTERNAL_INPUT_TYPE_CAMERA,//数据源类型
        var inputOrientation: Int = 0,//数据源朝向
        var deviceOrientation: Int = 0,////数据源为相机时候->手机设备朝向
        var cameraFacing: CameraFacingEnum = CameraFacingEnum.CAMERA_FRONT,//数据源为相机时候->前后置相机
        var inputTextureMatrix: TransformMatrixEnum = TransformMatrixEnum.CCROT270,//纹理旋转角度
        var inputBufferMatrix: TransformMatrixEnum = TransformMatrixEnum.CCROT0,//数据旋转角度
        var outputMatrixEnable: Boolean = false,//是否调用底层去设置outputMatrix
        var isRenderFaceBeautyOnly: Boolean = false,//数据旋转角度
        var isNeedBufferReturn: Boolean = false//是否需要合成后Buffer返回){}
    ) {
        var outputMatrix: TransformMatrixEnum = TransformMatrixEnum.CCROT0
            set(value) {
                field = value
                outputMatrixEnable = true
            }
    }

    fun printMsg(): String {
        val buffer = StringBuilder()
        buffer.append("width:$width  height:$height")
        if (texture == null) {
            buffer.append("    texture is null")
        } else {
            buffer.append("texId:${texture!!.texId}  inputTextureType:${texture!!.inputTextureType}")
        }
        if (imageBuffer == null) {
            buffer.append("    image is null")
        } else {
            buffer.append(
                "    inputBufferType:${imageBuffer!!.inputBufferType}  buffer Size:${imageBuffer!!.buffer?.size}  buffer1 Size:${imageBuffer!!.buffer1?.size} buffer2 Size:${imageBuffer!!.buffer2?.size}"
            )
        }
        buffer.append("    externalInputType:${renderConfig.externalInputType}")
        buffer.append("    inputOrientation:${renderConfig.inputOrientation}")
        buffer.append("    deviceOrientation:${renderConfig.deviceOrientation}")
        buffer.append("    cameraFacing:${renderConfig.cameraFacing}")
        buffer.append("    inputTextureMatrix:${renderConfig.inputTextureMatrix}")
        buffer.append("    inputBufferMatrix:${renderConfig.inputBufferMatrix}")
        buffer.append("    outputMatrix:${renderConfig.outputMatrix}")
        buffer.append("    isRenderFaceBeautyOnly:${renderConfig.isRenderFaceBeautyOnly}")
        buffer.append("    isNeedBufferReturn:${renderConfig.isNeedBufferReturn}")
        return buffer.toString()
    }


    fun clone(): RenderInputData {
        val inputData = RenderInputData(width, height)
        texture?.let {
            inputData.texture = FBTexture(it.inputTextureType, it.texId)
        }
        imageBuffer?.let {
            inputData.imageBuffer = FBImageBuffer(it.inputBufferType, it.buffer, it.buffer1, it.buffer2)
        }
        inputData.renderConfig.externalInputType = this.renderConfig.externalInputType
        inputData.renderConfig.inputOrientation = this.renderConfig.inputOrientation
        inputData.renderConfig.deviceOrientation = this.renderConfig.deviceOrientation
        inputData.renderConfig.cameraFacing = this.renderConfig.cameraFacing
        inputData.renderConfig.inputTextureMatrix = this.renderConfig.inputTextureMatrix
        inputData.renderConfig.inputBufferMatrix = this.renderConfig.inputBufferMatrix
        inputData.renderConfig.outputMatrixEnable= this.renderConfig.outputMatrixEnable
        inputData.renderConfig.outputMatrix = this.renderConfig.outputMatrix
        inputData.renderConfig.isRenderFaceBeautyOnly = this.renderConfig.isRenderFaceBeautyOnly
        inputData.renderConfig.isNeedBufferReturn = this.renderConfig.isNeedBufferReturn
        return inputData
    }
}
