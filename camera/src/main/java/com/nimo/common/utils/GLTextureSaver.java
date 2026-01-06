package com.nimo.common.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GLTextureSaver {

    /**
     * 保存 2D 纹理成 PNG 文件
     * @param textureId 2D 纹理 ID (fb2DTexId)
     * @param width 纹理宽度
     * @param height 纹理高度
     * @param filePath 保存路径（.png）
     */
    public static void saveTextureToPNG(int textureId, int width, int height, String filePath) {
        int[] frameBuffer = new int[1];

        // 1. 创建 FBO
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                textureId,
                0
        );

        // 2. 分配像素缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
        buffer.order(ByteOrder.nativeOrder());
        GLES20.glReadPixels(0, 0, width, height,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);

        // 3. 转成 Bitmap（并上下翻转）
        buffer.rewind();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);

        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f); // 上下翻转
        matrix.postRotate(-90);
        Bitmap flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        // 4. 保存到文件
        File file = new File(filePath);
        try (FileOutputStream out = new FileOutputStream(file)) {
            flippedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 5. 清理
        bitmap.recycle();
        flippedBitmap.recycle();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteFramebuffers(1, frameBuffer, 0);
    }
}

