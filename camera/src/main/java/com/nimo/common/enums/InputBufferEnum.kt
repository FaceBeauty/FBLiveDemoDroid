package com.nimo.common.enums

enum class InputBufferEnum(val type: Int) {
    FU_FORMAT_NV21_BUFFER(2),//	传入的数据为 NV21 数据格式
    FU_FORMAT_RGBA_BUFFER(4),//	传入的数据为 RGBA 数据格式
    FU_FORMAT_I420_BUFFER(13),//  传入的数据为 I420 数据格式
    FU_FORMAT_YUV_BUFFER(0)//  传入的数据为 YUV 数据格式
}