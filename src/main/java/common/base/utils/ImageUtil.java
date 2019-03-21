package common.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ******************(^_^)***********************
 * User: 11776610771@qq.com
 * Date: 2017/4/25
 * Time: 16:31
 * DESC: 使用Picasso来加载图片
 * ******************(^_^)***********************
 */

public class ImageUtil {
//    public static void loadImage(Context context, String picUrl, int newWidth, int newHeight, Drawable holderDrawable,
//                                 Drawable errorDrawable, ImageView targetIv
//            , Callback callback) {
//        RequestCreator loadRequest = loadImageRequest(context, picUrl,null,0);
//
//        if (newWidth > 0 && newHeight > 0) {
//            loadRequest.resize(newWidth, newHeight);
//            loadRequest.centerCrop();
//        }
//        else{
////            loadRequest.fit();
//        }
//        if (holderDrawable != null) {
//            loadRequest.placeholder(holderDrawable);
//        }
//        else{
//            loadRequest.noPlaceholder();
//        }
//        if (errorDrawable != null) {
//            loadRequest.error(errorDrawable);
//        }
//        loadRequest.noFade();
//        loadRequest.into(targetIv, callback);
//    }
public static void loadImage(Context context, String picUrl, int newWidth, int newHeight, Drawable holderDrawable,
                             Drawable errorDrawable, ImageView targetIv
        ,  RequestListener callback) {
    RequestBuilder requestBuilder = loadImageRequest(context, picUrl);
    RequestOptions options = new RequestOptions();
    if (newWidth > 0 && newHeight > 0) {
        options.override(newWidth,newHeight).centerCrop();
    }
    else{
        //            loadRequest.fit();
    }
    if (holderDrawable != null) {
        options.placeholder(holderDrawable);
    }
    else{
//        loadRequest.noPlaceholder();
    }
    if (errorDrawable != null) {
        options.error(errorDrawable);
    }
    options.dontAnimate();
    if (callback != null) {
        requestBuilder.listener(callback);
    }
    requestBuilder.apply(options)
            .into(targetIv);
}
    private static void throwCannotException(String reason) {
        throw new IllegalArgumentException("no " + reason + ",can't loca image pic...");
    }
//    private static RequestCreator loadImageRequest(Context context, String picUrl, File localPicFile,int localPicResId) {
//        if (Util.isEmpty(picUrl) && null == localPicFile && localPicResId <= 0) {
//            throwCannotException("pic path ");
//        }
//        Picasso picasso = Picasso.with(context);
//        if (!Util.isEmpty(picUrl)) {
//            return picasso.load(picUrl);
//        }
//        if (localPicFile != null) {
//            return picasso.load(localPicFile);
//        }
//        return picasso.load(localPicResId);
//    }

//    public static RequestCreator loadImageRequest(Context context, String picUrlOrPath) {
//        if (Util.isEmpty(picUrlOrPath)) {
//            throwCannotException("picUrl");
//        }
//        return loadImageRequest(context, picUrlOrPath, null, 0);
//    }
//    public static RequestCreator loadImageRequest(Context context, File localPicFile) {
//        if (null == localPicFile) {
//            throwCannotException("pic file");
//        }
//        return loadImageRequest(context, null, localPicFile, 0);
//    }
//    public static RequestCreator loadImageRequest(Context context, int localPicResId) {
//        if (localPicResId <= 0) {
//            throwCannotException("valid local pic res id");
//        }
//        return loadImageRequest(context, null, null, localPicResId);
//    }
    public static void loadImage(Context context, String picUrl, int newWidth, int newHeight, int holderDrawableResId,
                                 int errorDrawableResId, ImageView targetIv
            , RequestListener callback){
        Resources res = context.getResources();
        Drawable holderPic = null;
        if (holderDrawableResId > 0) {
            holderPic = res.getDrawable(holderDrawableResId);
        }
        Drawable errorDrawable = null;
        if (errorDrawableResId > 0) {
            errorDrawable = res.getDrawable(errorDrawableResId);
        }
        loadImage(context, picUrl, newWidth, newHeight,holderPic,
                errorDrawable,
                targetIv, callback);
    }

    public static void loadImage(Context context,String picUrl,int holderDrawableResId,
                                 int errorDrawableResId, ImageView targetIv
            , RequestListener callback){
        loadImage(context,picUrl,0,0,holderDrawableResId,errorDrawableResId,targetIv,callback);
    }

    public static void loadImage(Context context, String picUrl, int holderDrawableResId,
                                 int errorDrawableResId, ImageView targetIv) {
        loadImage(context,picUrl,holderDrawableResId,errorDrawableResId,targetIv,null);
    }

    public static void loadImage(Context context, String picUrl, ImageView targetIv, RequestListener callback) {
        loadImage(context, picUrl, 0, 0, targetIv, callback);
    }

    public static void loadImage(Context context, String picUrl, ImageView targetIv) {
        loadImage(context, picUrl, targetIv,null);
    }

    public static void loadResizeImage(Context context, String picUrl, int resizeW, int resizeH, ImageView targetIv) {
        loadImage(context, picUrl, resizeW, resizeH, null, null, targetIv, null);
    }

    public static void loadImage(Context context, Object model) {
        Glide.with(context).load(model);
    }

    public static RequestBuilder<Drawable> loadImageRequest(Context context, Object model) {
        return Glide.with(context).load(model);
    }

    public static RequestBuilder<Bitmap> loadBitmapeRequest(Context context, Object model) {
        return Glide.with(context).asBitmap().load(model);
    }

    public static void loadBitmap(Context context, Object model, Target target) {
        Glide.with(context).asBitmap().load(model).into(target);
    }

    public static void loadGif(Context context, int gifDrawableResId, ImageView ivTarget, int needPlayTime) {
        loadGifModel(context, gifDrawableResId, ivTarget, needPlayTime);
    }

    public static void loadGifModel(Context context, Object mayBeGifModel, ImageView ivTarget, final int needPlayTime) {
        if (mayBeGifModel == null) {
            return;
        }
        RequestBuilder<GifDrawable> gifDrawableBuilder = Glide.with(context).asGif();
        RequestListener<GifDrawable> loadGifDrawableListener = null;
        if (needPlayTime >= 1) {
            loadGifDrawableListener = new RequestListener<GifDrawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                    resource.setLoopCount(needPlayTime);
                    return false;
                }
            };
            gifDrawableBuilder.listener(loadGifDrawableListener);
        }
        if (mayBeGifModel instanceof Integer) {//还有:load(Bitmap xx);load(byte[]xxx);loadDrawable(Drawable xx);有差异
            Integer gifResId = (Integer) mayBeGifModel;
            if (gifResId != 0) {
                gifDrawableBuilder.load(gifResId);
            }
            else {
                return;
            }
        }
        else{
            gifDrawableBuilder.load(mayBeGifModel);
        }
        gifDrawableBuilder.into(ivTarget);
    }
    public static ColorDrawable createDefHolderColorDrawable(int theColor) {
        if (theColor <= 0) {
            theColor = Color.parseColor("#555555");
        }
        return new ColorDrawable(theColor);
    }
    //and so on 还可以重载出很多加载的方法

    public static final int BLUR_RADIUS = 50;

    @Nullable
    public static Bitmap blur(Bitmap sentBitmap) {
        try {
            return blur(sentBitmap, BLUR_RADIUS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Stack Blur v1.0 from
     * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     * <p>
     * Java Author: Mario Klingemann <mario at quasimondo.com>
     * http://incubator.quasimondo.com
     * created Feburary 29, 2004
     * Android port : Yahel Bouaziz <yahel at kayenko.com>
     * http://www.kayenko.com
     * ported april 5th, 2012
     * <p>
     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     * <p>
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     * <p>
     * If you are using this algorithm in your code please add
     * the following line:
     * <p>
     * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
     */
    private static Bitmap blur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return null;
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return bitmap;
    }

    /**
     * 将图片放大或缩小到指定尺寸
     */
    public static Bitmap resizeImage(Bitmap source, int w, int h) {
        int width = source.getWidth();
        int height = source.getHeight();
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
    }

    /**
     * 将图片剪裁为圆形
     */
    public static Bitmap createCircleImage(Bitmap source) {
        int length = Math.min(source.getWidth(), source.getHeight());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(length / 2, length / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

//    /**
//     * 使用Glide来下载【图片】文件
//     * 注意：非异步下载
//     * @param context Context
//     * @param fileUrl 要下载的文件地址
//     * @param targetLocalFilePath 目标存储
//     * @return file
//     */
//    private static File download(Context context, String fileUrl,String targetLocalFilePath) {
//        if (fileUrl == null || "".equals(fileUrl.trim())) {
//            return null;
//        }
//        File targetFile = null;
//        try {
//            targetFile = Glide.with(context).download(fileUrl).submit().get();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (targetLocalFilePath != null && !"".equals(targetLocalFilePath.trim())) {//表示需要复制到目标路径
//             //判断是否需要复制到目标
//            boolean isValidFile = targetFile != null && targetFile.length() > 1;
//            if (isValidFile) {
//                String downloadFileAbsPath = targetFile.getAbsolutePath();
//                CommonLog.e("info", "--->download() downloadFileAbsPath=" + downloadFileAbsPath);
//                if (!targetLocalFilePath.equals(downloadFileAbsPath)) {//一般不会相同
//                    File targetLocalFile = new File(targetLocalFilePath);
//                    boolean needCopyToTargetPath = true;
//
//                    boolean copySuc = copyFile(targetFile, targetLocalFile);
//                    if (copySuc) {
//                        targetFile.delete();
//                        targetFile = targetLocalFile;
//                    }
//                }
//            }
//        }
//        return targetFile;
//    }

    public static boolean copyFile(File srcFile, File targetFile) {
        if (srcFile == null || srcFile.length() < 1 || targetFile == null) {
            return false;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(targetFile, true);
            byte[] readBuf = new byte[1024];
            int hasReadLen = -1;
            while ((hasReadLen = fis.read(readBuf)) != -1) {
                byte[] readDatas = new byte[hasReadLen];
                System.arraycopy(readBuf, 0, readDatas, 0, hasReadLen);
                fos.write(readDatas);
            }
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static File downloadAndCopyToTarget(Context context, String fileUrl, String targetDirPath, String targetFileName, boolean deleteSrcFile) {
        File downloadResultFile = download(context, fileUrl);
        if (downloadResultFile == null) {
            return null;
        }
        boolean isTargetDirNull = isTextEmpty(targetDirPath);
        boolean isTargetFileNameNull = isTextEmpty(targetFileName);
        if (!isTargetDirNull) {//表示需要复制到目标路径
            boolean isDownloadFileValid = downloadResultFile.length() > 1;
            if (isDownloadFileValid) {//下载的文件有效
                String downloadedFileName = downloadResultFile.getName();
                CommonLog.e("info", "-->downloadAndCopyToTarget() downloadedFileName = " + downloadedFileName);
                String targetWholeFilePath = appendSeparator(targetDirPath);
                File targetFile = null;
                if (isTargetFileNameNull) {//指定的需要复制的文件名为空,则使用下载好了的文件的文件名
                    targetWholeFilePath += downloadedFileName;
                    targetFile = new File(targetWholeFilePath);
                    if (targetFile.exists() && targetFile.length() > 1) {
                        //这种情况下已经存在了，则不用复制了,直接返回
                        CommonLog.e("info", "-->downloadAndCopyToTarget() not need copy file...");
                        return targetFile;
                    }
                }
                else{
                    targetWholeFilePath += targetFileName;
                }
                targetFile = new File(targetWholeFilePath);
                boolean isCopySuc = copyFile(downloadResultFile, targetFile);
                if (isCopySuc) {
                    if (deleteSrcFile) {
                        downloadResultFile.delete();
                    }
                    downloadResultFile = targetFile;
                }
            }
        }
        return downloadResultFile;
    }
    public static File download(Context context, String fileUrl) {
        if (fileUrl == null || "".equals(fileUrl.trim())) {
            return null;
        }
        File targetFile = null;
        try {
            targetFile = Glide.with(context).download(fileUrl).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetFile;
    }

    private static boolean isTextEmpty(String text) {
        if (text == null || text.trim().length() == 0) {
            return true;
        }
        return false;
    }

    private static String appendSeparator(String text) {
        if (!isTextEmpty(text)) {
            if (!text.endsWith("/")) {
                return text + "/";
            }
        }
        return text;
    }
}
