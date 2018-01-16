package com.example.react_native_compress_image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Environment;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by chenjinpei on 2018/1/15.
 */

public class CompressImageModule extends ReactContextBaseJavaModule{
    public CompressImageModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "CompressImages";
    }
    // 以尺寸压缩
    @ReactMethod
    public void compressSize(ReadableMap Options, Promise promise) {
        WritableArray resultList = new WritableNativeArray();
        float maxHeight = 600;
        float maxWidth = 380;
        int quality = 60;
        String status = "success";
        String msg = "";
        // 判断是否包含最大宽度
        if (Options.hasKey("maxWidth")) {
            maxWidth = Options.getInt("maxWidth");
        }
        // 判断是否包含参数最大高度
        if (Options.hasKey("maxHeight")) {
            maxHeight = Options.getInt("maxHeight");
        }
        // 判断是否包含参数质量
        if (Options.hasKey("quality")) {
            if (0 < Options.getInt("quality") && Options.getInt("quality") < 100) {
                quality = Options.getInt("quality");
            }
        }
        // 判断是否有路径参数列表
        if (Options.hasKey("urlList")) {
            ReadableArray urlList = Options.getArray("urlList");
            // 循环遍历。压缩图片
            for (int i = 0; i < urlList.size(); i++) {
                String imgPath = urlList.getString(i);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true; //只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath, option); // 为null
                //获取到这个图片的原始宽度和高度
                int picWidth  = option.outWidth;
                int picHeight = option.outHeight;
                int inSampleSize = 1; // 默认像素压缩比例，
                if (picWidth > picHeight && picWidth > maxWidth) {//如果宽度大的话根据宽度固定大小缩放
                    inSampleSize = (int) Math.ceil((option.outWidth / maxWidth));
                } else if (picWidth < picHeight && picHeight > maxHeight) {//如果高度高的话根据宽度固定大小缩放
                    inSampleSize = (int) Math.ceil((option.outHeight / maxHeight));
                }
                if (inSampleSize <= 0)
                    inSampleSize = 1;
                option.inJustDecodeBounds = false;
                option.inSampleSize = inSampleSize;
                // 取缩放后的图片
                bitmap = BitmapFactory.decodeFile(imgPath, option);
                // 保存catch图片
                String saveName = "";
                if (imgPath.endsWith("jpg")) {
                    saveName = "compressCatch"+i+".jpg";
                } else {
                    saveName = "compressCatch"+i+".png";
                }
                String catchPath = saveBitmapToFile(getReactApplicationContext(),saveName, bitmap);
                resultList.pushString(catchPath);
            }
        } else {
            status = "error";
            msg = "must have urlList";
        }
        WritableMap map = Arguments.createMap();
        map.putArray("data", resultList);
        promise.resolve(map);
    }
    /*
    * ***************辅助函数，保存,bitmap到文件夹**********
    * */
    public String saveBitmapToFile(Context context, String fileName , Bitmap bitmap) {
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 60, fOut);
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
}
