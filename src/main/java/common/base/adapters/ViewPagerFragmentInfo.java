package common.base.adapters;

import android.os.Bundle;

/**
 * 提供给ViewPager的每个碎片的信息
 * <br/>
 * 2015年10月27日-下午3:45:51
 *
 * @author fee
 */
public final class ViewPagerFragmentInfo {
    public String fragmentTitle;
    public String tag;
    public Class<?> fragmentClass;
    public Bundle extraBundleData;

    public ViewPagerFragmentInfo(String fragmentTitle, String tag, Class<?> fragmentClass, Bundle extraData) {
        this.fragmentTitle = fragmentTitle;
        this.tag = tag;
        this.fragmentClass = fragmentClass;
        this.extraBundleData = extraData;
    }
}
