package com.example.react_native_compress_image;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
import java.util.ArrayList;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

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
    // 以质量压缩
    @ReactMethod
    public void compressQuality(ReadableMap Options, final Promise promise) {
        final WritableArray resultList = new WritableNativeArray();
        // 判断是否有路径参数列表
        if (Options.hasKey("urlList")) {
            ReadableArray urlList = Options.getArray("urlList");
            final ArrayList<String> photos = new ArrayList<>();
            // 循环遍历。添加图片
            for (int i = 0; i < urlList.size(); i++) {
                photos.add(urlList.getString(i));
            }
            // 调用Luban接口压缩图片
            Luban.with(getReactApplicationContext())
                   .load(photos)
                    .ignoreBy(100)
                    .setTargetDir(UtilFunction.getSaveUrl(getReactApplicationContext()))
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }
                        // 成功回调
                        @Override
                        public void onSuccess(File file) {
                            // 保存路径
                            resultList.pushString(file.getAbsolutePath());
                            // 判断是否对所有图片执行了操作
                            if (resultList.size() == photos.size()) {
                                WritableMap map = Arguments.createMap();
                                map.putString("status", "success");
                                map.putArray("urlList", resultList);
                                promise.resolve(map);
                            }
                        }
                        // 发生错误
                        @Override
                        public void onError(Throwable e) {
                            resultList.pushString(e.toString());
                            // 判断是否对所有图片执行了操作
                            if (resultList.size() == photos.size()) {
                                WritableMap map = Arguments.createMap();
                                map.putString("status", "success");
                                map.putArray("urlList", resultList);
                                promise.resolve(map);
                            }
                        }
                    }).launch();
        } else {
            WritableMap map = Arguments.createMap();
            map.putString("status", "success");
            map.putString("errorMsg", "must have urlList");
            promise.resolve(map);
        }
    }
    // 以尺寸压缩
    @ReactMethod
    public void compressSize(ReadableMap Options, Promise promise) {
        WritableArray resultList = new WritableNativeArray();
        WritableArray base64List = new WritableNativeArray();
        float maxHeight = 600;
        float maxWidth = 380;
        int quality = 60;
        Boolean saveImages = true; // 设置是否保存图片
        Boolean resultBase64 = false; // 设置是否返回base64
        String status = "success";
        String msg = "";
        UtilFunction util = new UtilFunction();
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
        // 判断是否有
        if (Options.hasKey("saveImages")) {
            saveImages = Options.getBoolean("saveImages");
        }
        if (Options.hasKey("resultBase64")) {
            resultBase64 = Options.getBoolean("resultBase64");
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
                // String catchPath = saveBitmapToFile(getReactApplicationContext(),saveName, bitmap);
                if (saveImages) {
                    String catchPath =  util.saveBitmapToFile(getReactApplicationContext(),saveName, bitmap, quality);
                    resultList.pushString(catchPath);
                } else {
                    String imgBase64 = util.bitmapToBase64(saveName, bitmap, quality);
                    base64List.pushString(imgBase64);
                }
                //需要返回base64
                if (resultBase64 && saveImages) {
                    String imgBase64 = util.bitmapToBase64(saveName, bitmap, quality);
                    base64List.pushString(imgBase64);
                }
            }
        } else {
            status = "error";
            msg = "must have urlList";
        }
        // 确定返回值
        WritableMap map = Arguments.createMap();
        map.putString("status", status);
        if (status.equals("error")) {
            map.putString("errorMsg", msg);
        } else {
            // 设置返回值
            if (saveImages) {
                map.putArray("urlList", resultList);
            } else {
                map.putArray("base64List", base64List);
            }
            if (resultBase64 && saveImages) {
                map.putArray("base64List", base64List);
            }
        }
        promise.resolve(map);
    }

}
