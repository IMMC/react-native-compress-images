package com.example.react_native_compress_image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Admin on 2018/1/16.
 */

public class UtilFunction {
    /*
    * ***************辅助函数，保存,bitmap到文件夹**********
    * */
    public String saveBitmapToFile(Context context, String fileName , Bitmap bitmap, int quality) {
        FileOutputStream fOut = null;
        try {
            File file = null;
            String fileDstPath = "";
            // 判断是否挂载SD卡
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                // 保存到sd卡的路径
                fileDstPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "compressCatch" + File.separator + fileName;
                // 判断父级目录是否存在
                File homeDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "compressCatch" + File.separator);
                if (!homeDir.exists()) {
                    homeDir.mkdirs();
                }
            } else {
                // 保存到file目录
                fileDstPath = context.getFilesDir().getAbsolutePath()
                        + File.separator + "compressCatch" + File.separator + fileName;

                File homeDir = new File(context.getFilesDir().getAbsolutePath()
                        + File.separator + "compressCatch" + File.separator);
                if (!homeDir.exists()) {
                    homeDir.mkdir();
                }
            }
            // 新建文件
            file = new File(fileDstPath);
            // 文件存在就删除
            if (file.exists()) {
                file.delete();
            }
            // 保存文件
            fOut = new FileOutputStream(file);
            if (fileDstPath.endsWith("jpg")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, fOut);
            }
            fOut.flush();
            fOut.close();
            bitmap.recycle();
            // 返回路径
            return fileDstPath;
        } catch (Exception e) {
            String sOut = "";
            StackTraceElement[] trace = e.getStackTrace();
            for (StackTraceElement s : trace) {
                sOut += "\tat " + s + "\r\n";
            }
            return "error";
        }
    }
    /*
    * **** bitmap转base64
    * */
    public static String bitmapToBase64(String fileName , Bitmap bitmap, int quality) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                if (fileName.endsWith("jpg")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
                }
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /*
    * ************获得路径*************
    * */
    public static String getSaveUrl (Context context) {
        String savePath = null;
        // 判断是否挂载SD卡
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            // 判断父级目录是否存在
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "compressCatch" + File.separator;
            File homeDir = new File(savePath);
            if (!homeDir.exists()) {
                homeDir.mkdirs();
            }
        } else {
            savePath = context.getFilesDir().getAbsolutePath()
                    + File.separator + "compressCatch" + File.separator;
            File homeDir = new File(savePath);
            if (!homeDir.exists()) {
                homeDir.mkdir();
            }
        }
        return savePath;
    }
}
