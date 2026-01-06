package com.nimo.common.entity

/**
 *
 *绘制矩阵
 *
 */
data class RenderFrameData(var texMatrix: FloatArray, var mvpMatrix: FloatArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RenderFrameData

        if (!texMatrix.contentEquals(other.texMatrix)) return false
        if (!mvpMatrix.contentEquals(other.mvpMatrix)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = texMatrix.contentHashCode()
        result = 31 * result + mvpMatrix.contentHashCode()
        return result
    }
}