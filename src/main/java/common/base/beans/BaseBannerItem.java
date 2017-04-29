package common.base.beans;

/**
 * ******************(^_^)***********************
 * User: 11776610771@qq.com
 * Date: 2017/4/25
 * Time: 16:20
 * DESC:
 * ******************(^_^)***********************
 */

public class BaseBannerItem extends BaseData {
    /**
     * 描述、标题
     */
    private String bannerItemTitle;
    /**
     * 如果是广告，则点击跳转的Url地址
     */
    private String bannerItemUrl;
    /**
     * banner的图片、封面
     */
    private String bannerItemCoverUrl;
    /**
     * banner的图片、封面在本地的资源ID，即本地[广告]图片
     */
    private int bannerCoverLocalResId;

    public String getBannerItemTitle() {
        return bannerItemTitle;
    }

    public String getBannerItemUrl() {
        return bannerItemUrl;
    }

    public String getBannerItemCoverUrl() {
        return bannerItemCoverUrl;
    }

    public int getBannerCoverLocalResId() {
        return bannerCoverLocalResId;
    }
}
