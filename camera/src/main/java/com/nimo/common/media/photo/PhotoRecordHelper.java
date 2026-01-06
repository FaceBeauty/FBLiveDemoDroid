package com.nimo.common.media.photo;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.util.Log;

import com.nimo.common.program.ProgramTexture2dWithAlpha;
import com.nimo.common.program.ProgramTextureOES;
import com.nimo.common.utils.GlUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;

import javax.microedition.khronos.opengles.GL10;

/**
 * DESC：拍照
 */
public class PhotoRecordHelper {

    private OnPhotoRecordingListener mOnPhotoRecordingListener;

    public PhotoRecordHelper(OnPhotoRecordingListener listener) {
        mOnPhotoRecordingListener = listener;
    }

    /**
     * 保存图片
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     * @param texWidth
     * @param texHeight
     */
    public void sendRecordingData(int texId, float[] texMatrix, float[] mvpMatrix, int texWidth, int texHeight) {
//        GLTextureSaver.saveTextureToPNG(texId, texWidth, texHeight, FBApplication.getApplication().getCacheDir().getAbsolutePath()+"/fb_image.jpg");
        glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, false,true);
    }

    /**
     * 保存图片
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     * @param texWidth
     * @param texHeight
     */
//    public void sendRecordingData(int texId, float[] texMatrix, float[] mvpMatrix, int texWidth, int texHeight, boolean isOes) {
//        glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, isOes,true);
//    }


    /**
     * 保存图片
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     * @param texWidth
     * @param texHeight
     */
    public void sendRecordingData(int texId, float[] texMatrix, float[] mvpMatrix, int texWidth, int texHeight, boolean isFrontCamera) {
        glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, false,true, isFrontCamera);
    }

    /**
     * 保存图片
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     * @param texWidth
     * @param texHeight
     */
    public void sendRecordingData(int texId, float[] texMatrix, float[] mvpMatrix, int texWidth, int texHeight,boolean isOes,boolean isAsync) {
        glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, isOes,isAsync);
    }

    /**
     * 将纹理转换成Bitmap
     *
     * @param texId
     * @param texMatrix
     * @param mvpMatrix
     * @param texWidth
     * @param texHeight
     * @param isOes
     */
    private void glReadBitmap(int texId, float[] texMatrix, float[] mvpMatrix, int texWidth, int texHeight, boolean isOes,boolean asyncBuildBitmap) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texWidth, texHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        int[] frameBuffers = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textures[0], 0);
        int[] viewport = new int[4];
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
        GLES20.glViewport(0, 0, texWidth, texHeight);
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if (isOes) {
            new ProgramTextureOES().drawFrame(texId, texMatrix, mvpMatrix);
        } else {
            new ProgramTexture2dWithAlpha().drawFrame(texId, texMatrix, mvpMatrix);
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(texWidth * texHeight * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        GLES20.glFinish();
        GLES20.glReadPixels(0, 0, texWidth, texHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buffer);
        GlUtil.checkGlError("glReadPixels");
        buffer.rewind();
        GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteTextures(1, textures, 0);
        GLES20.glDeleteFramebuffers(1, frameBuffers, 0);
        // ref: https://ww、w.programcreek.com/java-api-examples/?class=android.opengl.GLES20&method=glReadPixels
        if (asyncBuildBitmap) {
            new Thread(() -> {
                Bitmap bmp = Bitmap.createBitmap(texWidth, texHeight, Bitmap.Config.ARGB_8888);
                bmp.copyPixelsFromBuffer(buffer);
                Matrix matrix = new Matrix();
                matrix.preScale(1f, -1f);
                Bitmap finalBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                bmp.recycle();
                mOnPhotoRecordingListener.onRecordSuccess(finalBmp);
            }).start();
        } else {
            Bitmap bmp = Bitmap.createBitmap(texWidth, texHeight, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buffer);
            Matrix matrix = new Matrix();
            matrix.preScale(1f, -1f);
            Bitmap finalBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
            bmp.recycle();
            mOnPhotoRecordingListener.onRecordSuccess(finalBmp);
        }
    }

    private int fboId = 0;
    private int fboTexId = 0;
    private int fboWidth = 0;
    private int fboHeight = 0;
    private ByteBuffer pixelBuffer;

    private ProgramTextureOES programOes;
    private ProgramTexture2dWithAlpha program2d;

    private void initPrograms() {
        if (programOes == null) programOes = new ProgramTextureOES();
        if (program2d == null) program2d = new ProgramTexture2dWithAlpha();
    }
    private EGLContext lastEglContext = EGL14.EGL_NO_CONTEXT;
    private boolean checkContextLost() {
        EGLContext current = EGL14.eglGetCurrentContext();
        if (current == null || current == EGL14.EGL_NO_CONTEXT) {
            return false;
        }
        if (!current.equals(lastEglContext)) {
            lastEglContext = current;
            return true;
        }
        return false;
    }

    private void glReadBitmap(int texId, float[] texMatrix, float[] mvpMatrix, int texWidth, int texHeight, boolean isOes, boolean asyncBuildBitmap, boolean isFrontCamera) {
        if (checkContextLost()) {
            Log.w("PhotoRecordHelper", "EGLContext changed, release GL resources");

            releaseFbo();
            programOes = null;
            program2d = null;

            fboWidth = 0;
            fboHeight = 0;
        }

        initPrograms();

        // --- Step 1: 严格检查分辨率变化 ---
        if (fboWidth != texWidth || fboHeight != texHeight) {
            Log.d("fbo宽高", "glReadBitmap: "+"已执行");
            releaseFbo();

            // 新建纹理
            int[] tex = new int[1];
            GLES20.glGenTextures(1, tex, 0);
            fboTexId = tex[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexId);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                    texWidth, texHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

            // 新建FBO
            int[] fbo = new int[1];
            GLES20.glGenFramebuffers(1, fbo, 0);
            fboId = fbo[0];
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                    GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, fboTexId, 0);

            int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
            if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                throw new RuntimeException("Framebuffer not complete, status=" + status);
            }
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            pixelBuffer = ByteBuffer.allocateDirect(texWidth * texHeight * 4).order(ByteOrder.nativeOrder());
            fboWidth = texWidth;
            fboHeight = texHeight;
        }

        // --- Step 2: 渲染到FBO ---
        Log.d("glReadBitmap", "glReadBitmap: "+fboId);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        GLES20.glViewport(0, 0, texWidth, texHeight);
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (isOes) {
            programOes.drawFrame(texId, texMatrix, mvpMatrix);
        } else {
            program2d.drawFrame(texId, texMatrix, mvpMatrix);
        }

        // --- Step 3: 读取像素 ---
        pixelBuffer.clear();
        GLES20.glReadPixels(0, 0, texWidth, texHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
        GlUtil.checkGlError("glReadPixels");

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        // --- Step 4: 拷贝buffer，避免异步冲突 ---
        ByteBuffer safeBuffer = ByteBuffer.allocateDirect(texWidth * texHeight * 4).order(ByteOrder.nativeOrder());
        pixelBuffer.rewind();
        safeBuffer.put(pixelBuffer);
        safeBuffer.rewind();

        Runnable buildBitmapTask = () -> {
            Bitmap bmp = Bitmap.createBitmap(texWidth, texHeight, Bitmap.Config.ARGB_8888);
            Log.d("保存图片宽高", "glReadBitmap: "+texWidth + "  "+ texHeight);
            safeBuffer.rewind();
            bmp.copyPixelsFromBuffer(safeBuffer);

            Matrix matrix = new Matrix();
            if (isFrontCamera){
                matrix.preScale(1f, -1f);
                matrix.postRotate(-90);
            }else {
                matrix.postRotate(90);
            }


            Bitmap finalBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
            bmp.recycle();
            if (mOnPhotoRecordingListener != null) {
                mOnPhotoRecordingListener.onRecordSuccess(finalBmp);
            }
        };

        if (asyncBuildBitmap) {
            Executors.newSingleThreadExecutor().execute(buildBitmapTask);
        } else {
            buildBitmapTask.run();
        }
    }


    /** 释放 FBO 资源 */
    private void releaseFbo() {
        if (fboTexId != 0) {
            GLES20.glDeleteTextures(1, new int[]{fboTexId}, 0);
            fboTexId = 0;
        }
        if (fboId != 0) {
            GLES20.glDeleteFramebuffers(1, new int[]{fboId}, 0);
            fboId = 0;
        }
        pixelBuffer = null;
        fboWidth = fboHeight = 0;
    }

}

