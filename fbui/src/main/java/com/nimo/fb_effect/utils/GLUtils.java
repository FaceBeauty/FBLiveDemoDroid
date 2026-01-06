package com.nimo.fb_effect.utils;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GLUtils {
    // 通道顺序枚举（避免魔法值）
    public enum PixelFormat {
        RGBA, BGRA
    }

    // 检测 OpenGL 读取的像素通道顺序（必须在 GL 线程调用！）
    public static PixelFormat detectGLPixelFormat() {
        // 1. 创建一个 1x1 大小的直接缓冲区（只读取 1 个像素，效率高）
        ByteBuffer buffer = ByteBuffer.allocateDirect(4); // 1像素 × 4字节（RGBA/BGRA）
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(0);

        // 2. 用 OpenGL 读取 (0,0) 位置的 1x1 像素（此时需确保 GL 上下文已初始化）
        GLES20.glReadPixels(0, 0, 1, 1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
        buffer.rewind(); // 重置指针，准备读取

        // 3. 读取缓冲区的 4 个字节（对应通道顺序）
        byte b1 = buffer.get(0);
        byte b2 = buffer.get(1);
        byte b3 = buffer.get(2);
        byte b4 = buffer.get(3);

        // 4. 关键：判断通道顺序（前提：检测前需确保 GL 绘制的是「纯红色」像素）
        // 纯红色的正确 RGBA 是 (255,0,0,255)，BGRA 是 (0,0,255,255)
        int r = 0, g = 0, b = 0;
        // 先将 byte 转成 0-255 的无符号值
        int c1 = b1 & 0xFF;
        int c2 = b2 & 0xFF;
        int c3 = b3 & 0xFF;
        int c4 = b4 & 0xFF;

        // 检测哪个通道是 255（纯红色的 R 通道为 255）
        if (c1 == 255 && c2 == 0 && c3 == 0) {
            // 第一个字节是 R → RGBA 格式
            return PixelFormat.RGBA;
        } else if (c3 == 255 && c2 == 0 && c1 == 0) {
            // 第三个字节是 R → BGRA 格式（B G R A）
            return PixelFormat.BGRA;
        } else {
            // 特殊情况：默认按 RGBA 处理（兜底）
            return PixelFormat.RGBA;
        }
    }
}
