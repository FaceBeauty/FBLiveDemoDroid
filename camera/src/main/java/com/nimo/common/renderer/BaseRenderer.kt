package com.nimo.common.renderer

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.nimo.common.FBApplication
import com.nimo.common.entity.RenderFrameData
import com.nimo.common.entity.RenderInputData
import com.nimo.common.entity.RenderOutputData
import com.nimo.common.enums.ExternalInputEnum
import com.nimo.common.enums.InputBufferEnum
import com.nimo.common.enums.InputTextureEnum
import com.nimo.common.listener.OnGlRendererListener
import com.nimo.common.program.ProgramTexture2d
import com.nimo.common.utils.GlUtil
import com.nimo.common.utils.LimitFpsUtil
import com.nimo.common.utils.ScreenUtils
import com.nimo.facebeauty.FBEffect
import com.nimo.facebeauty.FBPreviewRenderer
import com.nimo.facebeauty.model.FBRotationEnum
import com.nimo.fb_effect.utils.FBUICacheUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 *
 * DESC：
 *
 */
abstract class BaseRenderer(protected var gLSurfaceView: GLSurfaceView?, protected var glRendererListener: OnGlRendererListener?) : GLSurfaceView.Renderer {
    val TAG = "BaseRenderer"

    /** 渲染矩阵  */
    val TEXTURE_MATRIX = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
    )

    val TEXTURE_MATRIX_ROTATE_90_FLIP_X = floatArrayOf(
        0f,  1f, 0f, 0f,
        1f,  0f, 0f, 0f,
        0f,  0f, 1f, 0f,
        0f,  0f, 0f, 1f
    )


    val CAMERA_TEXTURE_MATRIX = floatArrayOf(
        0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f
    )
    val CAMERA_TEXTURE_MATRIX_BACK = floatArrayOf(
        0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
    )

    //上下镜像 中间为坐标原点
    val TEXTURE_MATRIX_CCRO_FLIPV_0_CENTER = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
    )

    //上下镜像 并且左下角为坐标原点
    val TEXTURE_MATRIX_CCRO_FLIPV_0_LLQ = floatArrayOf(
        1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f
    )

    /** 纹理Program**/

    protected var programTexture2d: ProgramTexture2d? = null

    /**GLSurfaceView尺寸**/
    protected var surfaceViewWidth: Int = 1
    protected var surfaceViewHeight: Int = 1

    protected var init: Boolean = false
    public var isFrontCamera: Boolean = true
    protected var fbRotation: FBRotationEnum = FBRotationEnum.FBRotationClockwise270
    protected lateinit var previewRender: FBPreviewRenderer

    /**渲染参数配置**/
    @Volatile
    protected var currentRenderInputData = RenderInputData(0, 0)
    protected var originalTextId = 0
    protected var originalWidth = 0
    protected var originalHeight = 0
    protected var externalInputType = ExternalInputEnum.EXTERNAL_INPUT_TYPE_CAMERA//数据源类型
    protected var inputTextureType = InputTextureEnum.FU_ADM_FLAG_COMMON_TEXTURE//纹理类型
    protected var inputBufferType = InputBufferEnum.FU_FORMAT_NV21_BUFFER//数据类型
    protected var deviceOrientation = 90//手机设备朝向


    /** 渲染后纹理结果 **/
    protected var fb2DTexId = 0

    @Volatile
    protected var currentRenderOutputData: RenderOutputData? = null


    /**换算矩阵**/
    protected var defaultTexMatrix: FloatArray = TEXTURE_MATRIX.copyOf()
    protected var defaultMvpMatrix: FloatArray = TEXTURE_MATRIX.copyOf()
    protected var currentTexMatrix: FloatArray = TEXTURE_MATRIX.copyOf()
    protected var currentMvpMatrix: FloatArray = TEXTURE_MATRIX.copyOf()
    protected var originTexMatrix = TEXTURE_MATRIX.copyOf()
    protected var originMvpMatrix = TEXTURE_MATRIX.copyOf()
    protected var smallViewMatrix: FloatArray = TEXTURE_MATRIX.copyOf()
    protected var fbTexMatrx: FloatArray = TEXTURE_MATRIX.copyOf()

    private val mCachedImageBuffer = RenderOutputData.FBImageBuffer(0, 0)
    private val mCachedFBTexture = RenderOutputData.FBTexture(0, 0, 0)

    /**特效处理开关**/
    @Volatile
    protected var renderSwitch = true
    private var frameCount = 0
    private var frameRenderMinCount = 0

    /**activity是否进入Pause状态**/
    protected var isActivityPause = false

    /** 全身 avatar 相关 **/
    protected var drawSmallViewport = false
    protected val smallViewportWidth = ScreenUtils.dip2px(FBApplication.getApplication(), 90)
    protected val smallViewportHeight = ScreenUtils.dip2px(FBApplication.getApplication(), 160)
    protected var smallViewportX = 0
    protected var smallViewportY = 0
    protected val smallViewportHorizontalPadding = ScreenUtils.dip2px(FBApplication.getApplication(), 16)
    protected val smallViewportTopPadding = ScreenUtils.dip2px(FBApplication.getApplication(), 88)
    protected val smallViewportBottomPadding = ScreenUtils.dip2px(FBApplication.getApplication(), 100)
    protected var touchX = 0
    protected var touchY = 0


    /**
     * 设置渲染过渡帧数（原始数据-》渲染数据；避免黑屏）
     * @param count Int
     */
    fun setTransitionFrameCount(count: Int) {
        frameRenderMinCount = count
    }


    /**
     * 视图创建
     * 初始化Program，构建相机绑定纹理，打开相机，初始化
     * @param gl GL10
     * @param config EGLConfig
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GlUtil.logVersionInfo()
        programTexture2d = ProgramTexture2d()
        frameCount = 0
        surfaceCreated(gl, config)
        glRendererListener?.onSurfaceCreated()
    }

    protected abstract fun surfaceCreated(gl: GL10?, config: EGLConfig?)

    /**
     * 根据视图宽高，初始化配置
     * @param gl GL10
     * @param width Int
     * @param height Int
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//        previewRender  = FBPreviewRenderer(width, height)
//        previewRender.setPreviewRotation(270)
//        previewRender.create(true)
        GLES20.glViewport(0, 0, width, height)
        if (surfaceViewWidth != width || surfaceViewHeight != height) {
            surfaceViewWidth = width
            surfaceViewHeight = height
            surfaceChanged(gl, width, height)
        }
        smallViewportX = width - smallViewportWidth - smallViewportHorizontalPadding
        smallViewportY = smallViewportBottomPadding
        glRendererListener?.onSurfaceChanged(width, height)
    }

    protected abstract fun surfaceChanged(gl: GL10?, width: Int, height: Int)


    override fun onDrawFrame(gl: GL10?) {

        /* 应用是否在前台 */
        if (isActivityPause) {
            return
        }
        /* 确认是否只渲染图片 */
        if (mIsBitmapPreview) {
            drawBitmapFrame(mBitmap2dTexId, mBitmapTexMatrix, mBitmapMvpMatrix)
            return
        }

        updateTexImage()
        /* 确认环境是否正确 */
        if (!prepareRender(gl)) {
            return
        }
        /* 确认数据是否正确 */
        val inputData = buildRenderInputData()
        if ((inputData.imageBuffer == null || inputData.imageBuffer!!.buffer == null) && (inputData.texture == null || inputData.texture!!.texId <= 0)) {
            return
        }
        /* 特效合成，并通过回调确认最终渲染数据，合成数据，渲染矩阵  */
        if (renderSwitch && frameCount++ >= frameRenderMinCount) {
            val frameData = fbRenderFrameData()
            glRendererListener?.onRenderBefore(inputData)//特效合成前置处理
            onRenderBefore(inputData,frameData)
            currentRenderOutputData = generateRenderOutput(inputData)
            fb2DTexId = currentRenderOutputData!!.texture?.texId ?: 0
//            GLTextureSaver.saveTextureToPNG(fb2DTexId, currentRenderOutputData!!.texture!!.width, currentRenderOutputData!!.texture!!.height,FBApplication.getApplication().cacheDir.absolutePath+"/fb_image.jpg")
            onRenderAfter(currentRenderOutputData!!, frameData)
            glRendererListener?.onRenderAfter(currentRenderOutputData!!, frameData)
            currentTexMatrix = frameData.texMatrix
            currentMvpMatrix = frameData.mvpMatrix
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glViewport(0, 0, surfaceViewWidth, surfaceViewHeight)
        /* 渲染 */
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        drawRenderFrame(gl)
        /* 渲染完成回调 */
        glRendererListener?.onDrawFrameAfter()
        /* 循环调用 */
        if (externalInputType != ExternalInputEnum.EXTERNAL_INPUT_TYPE_CAMERA) {
            LimitFpsUtil.limitFrameRate()//循环调用
            gLSurfaceView?.requestRender()
        }
    }

    /*private var lastCameraFacingFront = false
    fun generateRenderOutput(input: RenderInputData): RenderOutputData {
        val width = input.width
        val height = input.height
        if (lastCameraFacingFront != isFrontCamera) {
            init = false
            lastCameraFacingFront = isFrontCamera
        }
        if (isFrontCamera){
            fbRotation = FBRotationEnum.FBRotationClockwise270
        }else {
            fbRotation = FBRotationEnum.FBRotationClockwise90
        }

        if (!init){
            FBEffect.shareInstance().releaseTextureOESRenderer()
            FBUICacheUtils.initCache(false)
            FBEffect.shareInstance().loadAIProcessor(0)
            FBEffect.shareInstance().loadAIProcessor(1)
            FBEffect.shareInstance().setRenderEnable(true)
            init = FBEffect.shareInstance().initTextureOESRenderer(
                width,
                height,
                fbRotation,
                true,
                5
            )
        }

        // 模拟输出纹理（直接复制输入的纹理 ID）
        val textureOutput = input.texture?.let {
            RenderOutputData.FBTexture(
//                texId = it.texId,
                texId = FBEffect.shareInstance().processTextureOES(it.texId),
                width = width,
                height = height
            )
        }

        val imageBufferOutput = input.imageBuffer?.let {
            RenderOutputData.FBImageBuffer(
                width = width,
                height = height,
                buffer = it.buffer?.clone(),
                buffer1 = it.buffer1?.clone(),
                buffer2 = it.buffer2?.clone(),
                stride = width,
                stride1 = width / 2,
                stride2 = width / 2
            )
        }

        return RenderOutputData(
            texture = textureOutput,
            image = imageBufferOutput
        )
    }*/

    protected var lastDeviceOrientation: Int = -1
    protected var lastCameraFacingFront: Boolean = false
    protected var lastFbRotation: FBRotationEnum? = null
    fun generateRenderOutput(input: RenderInputData): RenderOutputData {

        val width = input.width
        val height = input.height

        val newFbRotation = resolveFbRotation(deviceOrientation, isFrontCamera)

        if (needReInitFB(newFbRotation)) {
            init = false
        }

        fbRotation = newFbRotation

        if (!init) {
            reInitFB(width, height, fbRotation)
        }

        return RenderOutputData(
            texture = processTexture(input, width, height),
            image = processImageBuffer(input, width, height)
        )
    }

    protected open fun resolveFbRotation(
        deviceOrientation: Int,
        isFrontCamera: Boolean
    ): FBRotationEnum {
        return when (deviceOrientation) {
            0 -> FBRotationEnum.FBRotationClockwise0

            90 -> if (isFrontCamera)
                FBRotationEnum.FBRotationClockwise270
            else
                FBRotationEnum.FBRotationClockwise90

            180 -> FBRotationEnum.FBRotationClockwise180

            270 -> if (isFrontCamera)
                FBRotationEnum.FBRotationClockwise90
            else
                FBRotationEnum.FBRotationClockwise270

            else -> fbRotation
        }
    }

    protected open fun needReInitFB(newFbRotation: FBRotationEnum): Boolean {
        var needReInit = false

        if (lastCameraFacingFront != isFrontCamera) {
            lastCameraFacingFront = isFrontCamera
            needReInit = true
        }

        if (lastDeviceOrientation != deviceOrientation) {
            lastDeviceOrientation = deviceOrientation
            needReInit = true
        }

        if (lastFbRotation != newFbRotation) {
            lastFbRotation = newFbRotation
            needReInit = true
        }

        return needReInit
    }

    protected open fun reInitFB(
        width: Int,
        height: Int,
        fbRotation: FBRotationEnum
    ) {
        //todo --faceBeauty--start
        FBEffect.shareInstance().releaseTextureOESRenderer()

        FBUICacheUtils.initCache(false)

        FBEffect.shareInstance().loadAIProcessor(0)
        FBEffect.shareInstance().loadAIProcessor(1)
        FBEffect.shareInstance().setRenderEnable(true)

        init = FBEffect.shareInstance().initTextureOESRenderer(
            width,
            height,
            fbRotation,
            isFrontCamera,
            5
        )
        //todo --faceBeauty--end
    }

    protected open fun processTexture(
        input: RenderInputData,
        width: Int,
        height: Int
    ): RenderOutputData.FBTexture? {
        //todo --faceBeauty--start
        val inputTex = input.texture ?: return null
        return mCachedFBTexture.apply {
            this.texId = FBEffect.shareInstance().processTextureOES(inputTex.texId)
            this.width = width
            this.height = height
        }
        //todo --faceBeauty--end
    }

    protected open fun processImageBuffer(
        input: RenderInputData,
        width: Int,
        height: Int
    ): RenderOutputData.FBImageBuffer? {
        return input.imageBuffer?.let { inputBuf ->
            mCachedImageBuffer.width = width
            mCachedImageBuffer.height = height
            mCachedImageBuffer.stride = width
            mCachedImageBuffer.stride1 = width / 2
            mCachedImageBuffer.stride2 = width / 2

            mCachedImageBuffer.buffer = inputBuf.buffer
            mCachedImageBuffer.buffer1 = inputBuf.buffer1
            mCachedImageBuffer.buffer2 = inputBuf.buffer2

            mCachedImageBuffer
        }
    }


    protected open fun updateTexImage() {}

    open fun fbRenderFrameData(): RenderFrameData {
        return RenderFrameData(defaultTexMatrix.copyOf(), defaultMvpMatrix.copyOf())
    }

    /**渲染开始**/
    protected open fun onRenderBefore(input: RenderInputData,fuRenderFrameData: RenderFrameData) {}

    /**渲染结束**/
    protected open fun onRenderAfter(outputData: RenderOutputData, frameData: RenderFrameData){}

    protected abstract fun prepareRender(gl: GL10?): Boolean

    protected abstract fun buildRenderInputData(): RenderInputData

    protected abstract fun drawRenderFrame(gl: GL10?)

    /** 图片模式**/
    private var mIsBitmapPreview = false //是否只渲染图片
    private var mShotBitmap: Bitmap? = null //图片资源
    private var mBitmap2dTexId = 0
    private var mBitmapMvpMatrix = TEXTURE_MATRIX.copyOf()
    private var mBitmapTexMatrix = TEXTURE_MATRIX.copyOf()

    private fun drawBitmapFrame(texId: Int, texMatrix: FloatArray, mvpMatrix: FloatArray) {
        if (mBitmap2dTexId > 0) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            programTexture2d?.drawFrame(texId, texMatrix, mvpMatrix)
        }
    }

    /**
     * 渲染单张图片
     * @param bitmap Bitmap
     */
    protected fun drawImageTexture(bitmap: Bitmap) {
        mIsBitmapPreview = true
        mShotBitmap = bitmap
        gLSurfaceView?.queueEvent {
            deleteBitmapTexId()
            mBitmap2dTexId = GlUtil.createImageTexture(bitmap)
            mBitmapMvpMatrix =
                GlUtil.changeMvpMatrixCrop(surfaceViewWidth.toFloat(), surfaceViewHeight.toFloat(), bitmap.width.toFloat(), bitmap.height.toFloat())
            Matrix.scaleM(mBitmapMvpMatrix, 0, 1f, -1f, 1f)
        }
        gLSurfaceView?.requestRender()
    }

    /**
     * 移除单张图片渲染
     */
    protected fun dismissImageTexture() {
        mShotBitmap = null
        mIsBitmapPreview = false
        gLSurfaceView?.queueEvent {
            deleteBitmapTexId()
        }
        gLSurfaceView?.requestRender()
    }

    /**
     * 移除图片纹理
     */
    private fun deleteBitmapTexId() {
        if (mBitmap2dTexId > 0) {
            GlUtil.deleteTextures(intArrayOf(mBitmap2dTexId))
            mBitmap2dTexId = 0
        }
    }

    protected open fun destroyGlSurface() {
        deleteBitmapTexId()
        if (originalTextId != 0) {
            GlUtil.deleteTextures(intArrayOf(originalTextId))
            originalTextId = 0
        }
        if (fb2DTexId != 0) {
            GlUtil.deleteTextures(intArrayOf(fb2DTexId))
            fb2DTexId = 0
        }
        programTexture2d?.let {
            it.release()
            programTexture2d = null
        }
        glRendererListener?.onSurfaceDestroy()
    }


}