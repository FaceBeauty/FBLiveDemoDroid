package com.nimo.common.listener

import com.nimo.common.camera.CameraPreviewData

//相机回调数据
interface OnCameraListener {

    /**
     *
     * @param previewData FUCameraPreviewData 当前相机数据流
     */
    fun onPreviewFrame(previewData: CameraPreviewData)
}