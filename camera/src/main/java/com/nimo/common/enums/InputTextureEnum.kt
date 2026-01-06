package com.nimo.common.enums

enum class InputTextureEnum(val type: Int) {
    FU_ADM_FLAG_COMMON_TEXTURE(0),//	普通纹理
    FU_ADM_FLAG_EXTERNAL_OES_TEXTURE(1),//	传入的纹理为OpenGL external OES 纹理
    FU_ADM_FLAG_NV21_TEXTURE(4),//  传入的纹理为 NV21 数据格式
    FU_ADM_FLAG_I420_TEXTURE(8) //   传入的纹理为 I420 数据格式
}