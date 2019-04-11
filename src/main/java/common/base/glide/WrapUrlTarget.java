package common.base.glide;

import com.bumptech.glide.request.target.SimpleTarget;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/4/10<br>
 * Time: 15:32<br>
 * <P>DESC:
 * Glide加载图片到的Target，携带当前所加载的图片url地址
 * </p>
 * ******************(^_^)***********************
 */
public abstract class WrapUrlTarget<Z> extends SimpleTarget<Z> {
    private String targetUrl;
    public WrapUrlTarget(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    public void setTargetUrl(String urlInfo) {
        this.targetUrl = urlInfo;
    }


    public String getTargetUrl() {
        return targetUrl;
    }
}
