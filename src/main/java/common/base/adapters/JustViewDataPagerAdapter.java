package common.base.adapters;

import android.view.View;
import android.view.ViewGroup;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/2/18<br>
 * Time: 17:13<br>
 * <P>DESC:
 * 给ViewPager直接提供itemData为View的PagerAdapter
 * </p>
 * ******************(^_^)***********************
 */
public class JustViewDataPagerAdapter extends AbsPagerAdapter<View> {

    @Override
    protected Object convertAndInitItem(View itemData, ViewGroup container, int position) {
        container.addView(itemData);
        return itemData;
    }
}
