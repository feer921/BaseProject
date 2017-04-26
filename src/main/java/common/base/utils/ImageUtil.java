package common.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.File;

/**
 * ******************(^_^)***********************
 * User: 11776610771@qq.com
 * Date: 2017/4/25
 * Time: 16:31
 * DESC: 使用Picasso来加载图片
 * ******************(^_^)***********************
 */

public class ImageUtil {
    public static void loadImage(Context context, String picUrl, int newWidth, int newHeight, Drawable holderDrawable,
                                 Drawable errorDrawable, ImageView targetIv
            , Callback callback) {
        RequestCreator loadRequest = loadImageRequest(context, picUrl,null,0);

        if (newWidth > 0 && newHeight > 0) {
            loadRequest.resize(newWidth, newHeight);
        }
        if (holderDrawable != null) {
            loadRequest.placeholder(holderDrawable);
        }
        else{
            loadRequest.noPlaceholder();
        }
        if (errorDrawable != null) {
            loadRequest.error(errorDrawable);
        }
        loadRequest.centerCrop().into(targetIv, callback);
    }

    private static void throwCannotException(String reason) {
        throw new IllegalArgumentException("no " + reason + ",can't loca image pic...");
    }
    private static RequestCreator loadImageRequest(Context context, String picUrl, File localPicFile,int localPicResId) {
        if (Util.isEmpty(picUrl) && null == localPicFile && localPicResId <= 0) {
            throwCannotException("pic path ");
        }
        Picasso picasso = Picasso.with(context);
        if (!Util.isEmpty(picUrl)) {
            return picasso.load(picUrl);
        }
        if (localPicFile != null) {
            return picasso.load(localPicFile);
        }
        return picasso.load(localPicResId);
    }
    public static RequestCreator loadImageRequest(Context context, String picUrlOrPath) {
        if (Util.isEmpty(picUrlOrPath)) {
            throwCannotException("picUrl");
        }
        return loadImageRequest(context, picUrlOrPath, null, 0);
    }
    public static RequestCreator loadImageRequest(Context context, File localPicFile) {
        if (null == localPicFile) {
            throwCannotException("pic file");
        }
        return loadImageRequest(context, null, localPicFile, 0);
    }
    public static RequestCreator loadImageRequest(Context context, int localPicResId) {
        if (localPicResId <= 0) {
            throwCannotException("valid local pic res id");
        }
        return loadImageRequest(context, null, null, localPicResId);
    }
    public static void loadImage(Context context, String picUrl, int newWidth, int newHeight, int holderDrawableResId,
                                 int errorDrawableResId, ImageView targetIv
            , Callback callback){
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
            , Callback callback){
        loadImage(context,picUrl,0,0,holderDrawableResId,errorDrawableResId,targetIv,callback);
    }

    public static void loadImage(Context context, String picUrl, int holderDrawableResId,
                                 int errorDrawableResId, ImageView targetIv) {
        loadImage(context,picUrl,holderDrawableResId,errorDrawableResId,targetIv,null);
    }

    public static void loadImage(Context context, String picUrl, ImageView targetIv, Callback callback) {
        loadImage(context, picUrl, 0, 0, targetIv, callback);
    }

    public static void loadImage(Context context, String picUrl, ImageView targetIv) {
        loadImage(context, picUrl, targetIv,null);
    }
    //and so on 还可以重载出很多加载的方法
}
