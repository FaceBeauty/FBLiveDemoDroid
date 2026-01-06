package com.nimo.common.camera

import com.nimo.common.enums.CameraFacingEnum


data class CameraPreviewData(val buffer: ByteArray, val cameraFacing: CameraFacingEnum, val cameraOrientation: Int, val width: Int, val height: Int)