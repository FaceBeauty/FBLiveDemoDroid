package com.nimo.facebeauty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class FileUtils {

    public static final String DCIM_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    public static final String photoFilePath;
    public static final String videoFilePath;
    public static final String exportVideoDir;

    static {
        if (Build.FINGERPRINT.contains("Flyme")
                || Pattern.compile("Flyme", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("Meizu")
                || Build.MANUFACTURER.contains("MeiZu")) {
            photoFilePath = DCIM_FILE_PATH + File.separator + "Camera" + File.separator;
            videoFilePath = DCIM_FILE_PATH + File.separator + "Video" + File.separator;
        } else if (Build.FINGERPRINT.contains("vivo")
                || Pattern.compile("vivo", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("vivo")
                || Build.MANUFACTURER.contains("Vivo")) {
            photoFilePath = videoFilePath = Environment.getExternalStoragePublicDirectory("") + File.separator + "相机" + File.separator;
        } else {
            photoFilePath = videoFilePath = DCIM_FILE_PATH + File.separator + "Camera" + File.separator;
        }
        exportVideoDir = DCIM_FILE_PATH + File.separator + "FaceBeauty" + File.separator;
        createFileDir(photoFilePath);
        createFileDir(videoFilePath);
        createFileDir(exportVideoDir);
    }

    public static final String IMAGE_FORMAT_JPG = ".jpg";
    public static final String IMAGE_FORMAT_JPEG = ".jpeg";
    public static final String IMAGE_FORMAT_PNG = ".png";
    public static final String VIDEO_FORMAT_MP4 = ".mp4";

    /**
     * 创建文件夹
     *
     * @param path
     */
    public static void createFileDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 应用外部文件目录
     *
     * @return
     */
    public static File getExternalFileDir(Context context) {
        File fileDir = context.getExternalFilesDir(null);
        if (fileDir == null) {
            fileDir = context.getFilesDir();
        }
        return fileDir;
    }

    /**
     * 应用下缓存文件目录
     *
     * @return
     */
    public static File getCacheFileDir(Context context) {
        File fileDir = context.getCacheDir();
        if (fileDir == null) {
            fileDir = context.getFilesDir();
        }
        return fileDir;
    }

    /**
     * 获取当前时间日期
     *
     * @return
     */
    public static String getDateTimeString() {
        GregorianCalendar now = new GregorianCalendar();
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(now.getTime());
    }

    /**
     * 选中图片
     *
     * @param activity Activity
     */
    public static void pickImageFile(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 选中视频
     *
     * @param activity Activity
     *                 回调可参考下方
     */
    public static void pickVideoFile(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 校验文件是否是图片
     *
     * @param path String
     * @return Boolean
     */
    public static Boolean checkIsImage(String path) {
        String name = new File(path).getName().toLowerCase();
        return (name.endsWith(IMAGE_FORMAT_PNG) || name.endsWith(IMAGE_FORMAT_JPG)
                || name.endsWith(IMAGE_FORMAT_JPEG));
    }

    /**
     * 校验文件是否是视频
     *
     * @param path String
     * @return Boolean
     */
    public static Boolean checkIsVideo(Context context, String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, Uri.fromFile(new File(path)));
            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            return "yes".equals(hasVideo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取Uri文件绝对路径
     *
     * @param context: Context
     * @param uri      Uri
     * @return String
     */
    public static String getFilePathByUri(Context context, Uri uri) {
        if (uri == null) return null;
        return Uri2PathUtil.getRealPathFromUri(context, uri);
    }

    /**
     * 构造视频文件名称
     *
     * @return
     */
    public static String getCurrentVideoFileName() {
        return getDateTimeString() + VIDEO_FORMAT_MP4;
    }


    /**
     * 构造图片文件名称
     *
     * @return
     */
    public static String getCurrentPhotoFileName() {
        return getDateTimeString() + IMAGE_FORMAT_JPG;
    }

    /**
     * 将Bitmap文件保存到相册
     *
     * @param bitmap Bitmap
     */
    public static String addBitmapToAlbum(Context context, Bitmap bitmap) {
        if (bitmap == null) return null;
        FileOutputStream fos = null;
        File dcimFile;

        File fileDir = new File(exportVideoDir);
        if (fileDir.exists()) {
            dcimFile = new File(exportVideoDir, getCurrentPhotoFileName());
        } else {
            dcimFile = new File(photoFilePath, getCurrentPhotoFileName());
        }
        if (dcimFile.exists()) {
            dcimFile.delete();
        }
        try {
            fos = new FileOutputStream(dcimFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dcimFile)));
        return dcimFile.getAbsolutePath();
    }


    /**
     * 将视频文件保存到相册
     *
     * @param videoFile File
     * @return Uri?
     */

    public static String addVideoToAlbum(Context context, File videoFile) {
        if (videoFile == null) return null;
        File fileDir = new File(exportVideoDir);
        File dcimFile;
        if (fileDir.exists()) {
            dcimFile = new File(exportVideoDir, getCurrentVideoFileName());
        } else {
            dcimFile = new File(videoFilePath, getCurrentVideoFileName());
        }
        if (dcimFile.exists()) {
            dcimFile.delete();
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(videoFile));
            bos = new BufferedOutputStream(new FileOutputStream(dcimFile));
            byte[] bytes = new byte[1024 * 10];
            int length;
            while ((length = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, length);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dcimFile)));
        return dcimFile.getAbsolutePath();
    }

}
