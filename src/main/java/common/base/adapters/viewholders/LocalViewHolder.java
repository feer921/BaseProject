package common.base.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import com.chad.library.adapter.base.BaseViewHolder;

import common.base.utils.CheckUtil;
import common.base.utils.ImageUtil;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
public class LocalViewHolder extends BaseViewHolder {
    protected final String TAG = getClass().getSimpleName();
    public LocalViewHolder(View view) {
        super(view);
    }

    public void commonLoadImgData(@IdRes int imgViewId, String imgUrl, @DrawableRes int defHoldImgRes,boolean useAppContext){
        ImageView ivImg = getView(imgViewId);
        Context useContext = null;
        if (!useAppContext) {
            if (ivImg != null) {
                useContext = ivImg.getContext();
            }
        }
        loadImgDataWithContext(useContext,ivImg,imgUrl,defHoldImgRes);
    }

    /**
     * 使用 Application 级的Context 加载图片
     * @param imgViewId
     * @param imgUrl
     * @param defHoldImgRes
     */
    public void commonLoadImgData(@IdRes int imgViewId, String imgUrl, @DrawableRes int defHoldImgRes) {
        ImageView ivImg = getView(imgViewId);
        commonLoadImgData(ivImg, imgUrl, defHoldImgRes);
    }

    public void commonLoadImgData(ImageView imgView, String imgUrl, @DrawableRes int defHoldImgRes) {
        loadImgDataWithContext(null, imgView, imgUrl, defHoldImgRes);
    }

    public void loadImgDataWithContext(Context assignContext, ImageView imgView, String imgUrl, @DrawableRes int defHoldImgRes) {
        if (imgView != null) {
            Context context;
//            Context context = imgView.getContext().getApplicationContext();//???都换成 Application类型的Context
            if (mContext == null) {
                mContext = imgView.getContext();
                appContext = mContext.getApplicationContext();
            }
            if (assignContext == null) {
                assignContext = appContext;
            }
            context = assignContext;
            if (isEmpty(imgUrl)) {
                imgView.setImageResource(defHoldImgRes);
            } else {
                if (CheckUtil.isGifImage(imgUrl)) {
                    ImageUtil.loadGifModel(context, imgUrl, defHoldImgRes, imgView, 0);
                } else {
                    ImageUtil.loadImage(context, imgUrl, defHoldImgRes, defHoldImgRes, imgView);
                }
            }
        }
    }
    public boolean isEmpty(CharSequence charSequence) {
        return CheckUtil.isEmpty(charSequence);
    }
}
