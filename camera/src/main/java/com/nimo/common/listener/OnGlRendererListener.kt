package com.nimo.common.listener

import com.nimo.common.entity.RenderFrameData
import com.nimo.common.entity.RenderInputData
import com.nimo.common.entity.RenderOutputData


interface OnGlRendererListener {


    /**
     * GLSurfaceView.Renderer onSurfaceCreated完成
     */
    fun onSurfaceCreated()

    /**
     * GLSurfaceView.Renderer onSurfaceChanged 完成
     */
    fun onSurfaceChanged(width: Int, height: Int)


    /**
     * 当前渲染的数据流（GL线程回调）
     * @param inputData FURenderInputData 原始数据
     */
    fun onRenderBefore(inputData: RenderInputData?)


    /**
     * 当前渲染的数据流（GL线程回调）
     * @param outputData RenderOutputData 特效处理后数据
     * @param frameData RenderFrameData 即将渲染矩阵
     */
    fun onRenderAfter(outputData: RenderOutputData, frameData: RenderFrameData)

    /**
     * 视图渲染完成
     */
    fun onDrawFrameAfter()

    /**
     * 视图销毁
     */
    fun onSurfaceDestroy()

}